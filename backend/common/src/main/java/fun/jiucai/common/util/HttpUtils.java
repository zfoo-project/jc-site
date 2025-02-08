/*
 * Copyright (C) 2020 The zfoo Authors
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package fun.jiucai.common.util;

import com.zfoo.monitor.util.OSUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author godotg
 */
public abstract class HttpUtils {

    public static RestTemplate restTemplate;
    public static final HttpHeaders HEADERS = new HttpHeaders();
    static {
        restTemplate = new RestTemplate();

        // 从浏览器复制过来的header
        var headersString = """
                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
                Accept-Encoding: gzip, deflate, br, zstd
                Accept-Language: zh-CN,zh;q=0.9
                Cache-Control: max-age=0
                Connection: keep-alive
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

        for(var header : headersString.split(FileUtils.LS_REGEX)) {
            var splits = header.split(StringUtils.COLON_REGEX);
            HEADERS.add(StringUtils.trim(splits[0]), StringUtils.trim(splits[1]));
        }
    }

    public static String getNoHeaders(String url) {
        return restTemplate.getForObject(url, String.class);
    }

    public static String getWithHeaders(String url, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for(var header : headers.entrySet()) {
            httpHeaders.add(header.getKey(), header.getValue());
        }
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class);
        String responseBody = response.getBody();
        return responseBody;
    }

    public static String get(String url) throws IOException, InterruptedException {
        HttpEntity<String> entity = new HttpEntity<>(HEADERS);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class);
        String responseBody = response.getBody();
        return responseBody;
    }

    public static String getWithGzip(String url) throws IOException, InterruptedException {
        HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");

        for (var header : HEADERS.entrySet()) {
            con.setRequestProperty(header.getKey(), header.getValue().get(0));
        }

        InputStream inputStream = con.getInputStream();
        InputStream decodedStream = new GZIPInputStream(inputStream);

        BufferedReader reader = new BufferedReader(new InputStreamReader(decodedStream));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        con.disconnect();
        return builder.toString();
    }

    public static byte[] getBytes(String url) {
        return restTemplate.getForObject(url, byte[].class);
    }

    public static String post(String url, Object jsonObject) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        var requestBody = JsonUtils.object2String(jsonObject);
        var requestEntity = new HttpEntity<String>(requestBody, headers);
        var responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        var response = responseEntity.getBody();
        return response;
    }

    public static String formatJson(String json) {
        json = StringUtils.substringAfterFirst(json, "(");
        json = StringUtils.substringBeforeLast(json, ")");
        return json;
    }

    public static String puppeteer(String url, String spiderPath) {
        var command = StringUtils.format("node {} {}", spiderPath, url);
        var str = OSUtils.execCommand(command);
        return str;
    }



    /**
     * 主要检查url是否已http开头，如果不已http开头，则认为是非法的url
     */
    public static boolean isHttpUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * 检查文件的url是否合法
     */
    public static boolean isHttpUrls(List<String> urlLinks) {
        if (CollectionUtils.isEmpty(urlLinks)) {
            return true;
        }
        for (var urlLink : urlLinks) {
            if (!isHttpUrl(urlLink)) {
                return false;
            }
        }
        return true;
    }
}
