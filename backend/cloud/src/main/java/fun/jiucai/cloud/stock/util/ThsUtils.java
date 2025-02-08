package fun.jiucai.cloud.stock.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.cloud.model.common.StockPriceAndRise;
import fun.jiucai.cloud.util.SpiderUtils;
import fun.jiucai.common.util.HttpUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public abstract class ThsUtils {

    private static final Logger logger = LoggerFactory.getLogger(ThsUtils.class);

    public static RestTemplate restTemplate;
    public static final HttpHeaders HEADERS = new HttpHeaders();

    static {
        restTemplate = new RestTemplate();

        // 从浏览器复制过来的header
        var headersString = """
                referer: https://q.10jqka.com.cn/
                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
                Accept-Encoding: gzip, deflate, br, zstd
                Accept-Language: zh-CN,zh;q=0.9
                Cache-Control: max-age=0
                Connection: keep-alive
                Host: q.10jqka.com.cn
                Sec-Fetch-Dest: document
                Sec-Fetch-Mode: navigate
                Sec-Fetch-Site: same-origin
                Sec-Fetch-User: ?1
                Upgrade-Insecure-Requests: 1
                User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36
                sec-ch-ua: "Google Chrome";v="125", "Chromium";v="125", "Not.A/Brand";v="24"
                sec-ch-ua-mobile: ?0
                sec-ch-ua-platform: "Windows"
                """;

        for (var header : headersString.split(FileUtils.LS_REGEX)) {
            var splits = header.split(StringUtils.COLON_REGEX);
            HEADERS.add(StringUtils.trim(splits[0]), StringUtils.trim(splits[1]));
        }
    }

    public static String get(String url) {
        HttpEntity<String> entity = new HttpEntity<>(HEADERS);
        var response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        var bytes = response.getBody();
        // gbk编码格式
        var responseHeaders = response.getHeaders();
        var contentTypes = responseHeaders.get("Content-Type");
        if (CollectionUtils.isNotEmpty(contentTypes) && contentTypes.stream().anyMatch(it -> it.toLowerCase().contains("gbk"))) {
            return new String(bytes, Charset.forName("gbk"));
        }
        String responseBody = StringUtils.bytesToString(response.getBody());
        return responseBody;
    }

    public static String getNoHeaders(String url) {
        return restTemplate.getForObject(url, String.class);
    }


    // -----------------------------------------------------------------------------------------------------------------
    public static List<Pair<Integer, String>> gn() {
        var url = "http://q.10jqka.com.cn/gn/";
        var gnUrl = "http://q.10jqka.com.cn/gn/detail/code/";
        var html = getNoHeaders(url);


        var document = Jsoup.parse(html);
        var map = new HashMap<Integer, String>();
        // 网页最上面的概念
        var elements = document.getElementsByAttributeValue("class", "cate_items");
        for (var cateEle : elements) {
            var conceptEle = cateEle.children();
            for (var ele : conceptEle) {
                var code = StringUtils.substringAfterFirst(ele.attr("href"), gnUrl);
                code = StringUtils.substringBeforeFirst(code, "/");

                var codeName = ele.text();
                map.put(Integer.parseInt(StringUtils.trim(code)), codeName);
            }
        }

        // 网页最下方的概念
        var elementTables = document.getElementsByAttributeValue("class", "m-table m-pager-table");
        for (var tableEle : elementTables) {
            var aTags = tableEle.getElementsByTag("a");
            for (var ele : aTags) {
                if (!ele.attr("href").startsWith(gnUrl)) {
                    continue;
                }
                var code = StringUtils.substringAfterFirst(ele.attr("href"), gnUrl);
                code = StringUtils.substringBeforeFirst(code, "/");

                var codeName = ele.text();
                map.put(Integer.parseInt(StringUtils.trim(code)), codeName);
            }
        }

        // 全网页扫描
        for (var ele : document.getElementsByTag("a")) {
            if (!ele.attr("href").startsWith(gnUrl)) {
                continue;
            }
            var code = StringUtils.substringAfterFirst(ele.attr("href"), gnUrl);
            code = StringUtils.substringBeforeFirst(code, "/");

            var codeName = ele.text();
            map.put(Integer.parseInt(StringUtils.trim(code)), codeName);
        }

        return map.entrySet().stream().map(it -> new Pair<>(it.getKey(), it.getValue())).collect(Collectors.toList());
    }

    public static List<Pair<Integer, String>> thshy() {
        var url = "http://q.10jqka.com.cn/thshy/";
        var list = new ArrayList<Pair<Integer, String>>();

        var html = getNoHeaders(url);
        var document = Jsoup.parse(html);
        var elements = document.getElementsByAttributeValue("class", "cate_items");

        for (var cateEle : elements) {
            var conceptEle = cateEle.children();
            for (var ele : conceptEle) {
                var code = StringUtils.substringAfterFirst(ele.attr("href"), "http://q.10jqka.com.cn/thshy/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var codeName = ele.text();
                list.add(new Pair<>(Integer.parseInt(StringUtils.trim(code)), codeName));
            }
        }
        return list;
    }

    public static List<Pair<Integer, String>> allConcepts() {
        var list = new ArrayList<Pair<Integer, String>>();
        list.addAll(gn());
        list.addAll(thshy());
        return list;
    }


    // -----------------------------------------------------------------------------------------------------------------
    public static String conceptHtmlUrl(int code) {
        var stockCode = StockUtils.formatCode(code);

        var url = stockCode.startsWith("3") ? StringUtils.format("https://q.10jqka.com.cn/gn/detail/code/{}/", stockCode) : StringUtils.format("https://q.10jqka.com.cn/thshy/detail/code/{}/", stockCode);

        return url;
    }

    public static StockPriceAndRise conceptPriceAndRise(int code) {
        try {
            var url = conceptHtmlUrl(code);
            var responseBody = HttpUtils.puppeteer(url, SpiderUtils.spiderPath());
            var document = Jsoup.parse(responseBody);
            var content = document.getElementsByClass("board-zdf").text();
            var splits = content.split(StringUtils.SPACE_REGEX);
            var price = splits[0];
            var rise = splits[1];
            return StockPriceAndRise.valueOf(Float.parseFloat(price), Float.parseFloat(StringUtils.substringBeforeFirst(rise, "%")));
        } catch (Exception e) {
            logger.error("concept ths error", e);
        }
        return new StockPriceAndRise();
    }


    // -----------------------------------------------------------------------------------------------------------------
    @SneakyThrows
    public static List<StockResult> rank100() {
        var url = "https://dq.10jqka.com.cn/fuyao/hot_list_data/out/hot_list/v1/stock?stock_type=a&type=hour&list_type=normal";
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest
                .newBuilder(URI.create(url))
                .GET()
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var response = client.send(request, responseBodyHandler);
        var responseBody = response.body();
        var rankResponse = JsonUtils.string2Object(responseBody, RankResponse.class);
        var list = new ArrayList<StockResult>();
        for (var stock : rankResponse.data.stocks) {
            list.add(stock);
        }
        return list;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RankResponse {
        @JsonProperty("status_code")
        private int statusCode;
        @JsonProperty("status_msg")
        private String message;
        private RankStockData data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RankStockData {
        @JsonProperty("stock_list")
        private List<StockResult> stocks;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockResult {
        private int code;
        private float rate;
        @JsonProperty("hot_rank_chg")
        private int rankChange;
        private String name;
        private String analyse;
    }
}
