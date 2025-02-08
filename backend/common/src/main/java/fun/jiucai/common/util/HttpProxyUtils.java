package fun.jiucai.common.util;

import com.zfoo.monitor.util.OSUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

/**
 * @author godotg
 */
public abstract class HttpProxyUtils {

    public static final Proxy PROXY_SOCKET = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 10808));
    public static final Proxy PROXY_HTTP = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));

    public static RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
        var simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setProxy(PROXY_SOCKET);
        restTemplate.setRequestFactory(simpleClientHttpRequestFactory);
    }

    public static String get(String url) throws IOException, InterruptedException {
        return restTemplate.getForObject(url, String.class);
    }

    public static String get(String url, Map<String, String> headerMap) throws IOException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        if (CollectionUtils.isNotEmpty(headerMap)) {
            for (var entry : headerMap.entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();
        return responseBody;
    }

    public static byte[] getBytes(String url) {
        return restTemplate.getForObject(url, byte[].class);
    }

    public static String post(String url, Object jsonObject) {
        return post(url, jsonObject, null);
    }

    public static String post(String url, Object jsonObject, Map<String, String> headerMap) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        if (CollectionUtils.isNotEmpty(headerMap)) {
            for (var entry : headerMap.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }

        var requestBody = JsonUtils.object2String(jsonObject);

        var requestEntity = new HttpEntity<String>(requestBody, headers);
        var responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        var response = responseEntity.getBody();
        return response;
    }

    public static String postWithString(String url, String json, Map<String, String> headerMap) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        if (CollectionUtils.isNotEmpty(headerMap)) {
            for (var entry : headerMap.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }

        var requestEntity = new HttpEntity<String>(json, headers);
        var responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        var response = responseEntity.getBody();
        return response;
    }

    public static String post(String url, Map<String, String> headerMap) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        if (CollectionUtils.isNotEmpty(headerMap)) {
            for (var entry : headerMap.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }

        var requestEntity = new HttpEntity<String>(headers);
        var responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        var response = responseEntity.getBody();
        return response;
    }

    public static boolean isCrossFireWall() {
        // 判断当前的v2ray网络是否可用
        var pingGoogle = OSUtils.execCommand("curl -x socks5://127.0.0.1:10808 https://www.google.com -v");
        if (pingGoogle.length() > 1_0000) {
            return true;
        }
        return false;
    }
}
