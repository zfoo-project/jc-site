package fun.jiucai.home.manager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zfoo.net.util.security.MD5Utils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.LazyCache;
import com.zfoo.scheduler.util.TimeUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author godotg
 */
@Slf4j
@Component
public class TranslateManager {

    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    @Value("${jiucai.translate.appid}")
    private String appid;
    @Value("${jiucai.translate.securityKey}")
    private String securityKey;

    public LazyCache<String, String> caches = new LazyCache<>(10000, 30 * TimeUtils.MILLIS_PER_MINUTE, 10 * TimeUtils.MILLIS_PER_MINUTE, null);

    public boolean isContainChinese(String str) {
        if (str == null) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                // 如果字符是中文，则返回true
                return true;
            }
        }
        return false;
    }

    public String cn2en(String query) {
        query = StringUtils.trim(query);
        if (!isContainChinese(query)) {
            return query;
        }
        var cacheTranslate = caches.get(query);
        if (cacheTranslate != null) {
            return cacheTranslate;
        }
        var en = translate(query, "zh", "en");
        caches.put(query, en);
        return en;
    }

    public String translate(String query, String from, String to) {
        Map<String, String> params = buildParams(query, from, to);
        try {
            return get(TRANS_API_HOST, params);
        } catch (Exception e) {
            log.error("百度翻译未知异常", e);
        }
        return query;
    }

    protected static final int SOCKET_TIMEOUT = 10000; // 10S
    protected static final String GET = "GET";

    @SneakyThrows
    public static String get(String host, Map<String, String> params) {
        String sendUrl = getUrlWithQueryString(host, params);

        URL uri = new URL(sendUrl); // 创建URL对象
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

        conn.setConnectTimeout(SOCKET_TIMEOUT); // 设置相应超时
        conn.setRequestMethod(GET);

        // 读取服务器的数据
        InputStream is = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }

        String json = builder.toString();

        IOUtils.closeIO(br, is);
        conn.disconnect(); // 断开连接

        var result = JsonUtils.string2Object(json, TranslateResult.class);
        return result.getResults().get(0).getDst();
    }

    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }

    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5Utils.strToMD5(src).toLowerCase());

        return params;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TranslateResult {
        private String from;
        private String to;
        @JsonProperty("trans_result")
        private List<Result> results;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Result {
        private String src;
        private String dst;
    }

}
