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

package fun.jiucai.home.service.seo;

import com.aliyun.oss.model.ObjectMetadata;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zfoo.event.manager.EventBus;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.anno.Scheduler;
import fun.jiucai.home.manager.OssManager;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * @author godotg
 */
@Slf4j
@Component
public class SeoService {

    @Autowired
    private OssManager ossManager;

    public void uploadHtml(long newsId, String html) {
        var objectName = StringUtils.format("ac/{}", newsId);
        var objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("text/html");
        ossManager.upload(StringUtils.bytes(html), objectName, objectMetadata);
    }

    public void uploadJson(long newsId, String json) {
        var objectName = StringUtils.format("aj/{}", newsId);
        ossManager.upload(StringUtils.bytes(json), objectName);
    }

    // ------------------------------------------------百度 seo--------------------------------------------------
    @Value("${jiucai.seo.baidu}")
    private String baiDuAutoSeoApi;
    private int baiduSeoRemain;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BaiDuSeoResponse {
        // 成功推送的url条数
        @JsonProperty("success")
        private int success;
        // 当天剩余的可推送url条数
        @JsonProperty("remain")
        private int remain;
        // 由于不是本站url而未处理的url列表
        @JsonProperty("not_same_site")
        private List<String> notSameSite;
        // 不合法的url列表
        @JsonProperty("not_valid")
        private List<String> notValid;
        // 错误码，与状态码相同
        @JsonProperty("error")
        private int error;
        // 错误描述
        @JsonProperty("message")
        private String message;
    }

    @Scheduler(cron = "0 0 0 * * ?")
    public void cronClsSpider00() {
        baiduSeoRemain = 100;
    }

    public void baiduSeo(long newsId) {
        EventBus.asyncExecute(() -> doBaiduSeo(newsId));
    }

    @SneakyThrows
    private void doBaiduSeo(long newsId) {
        if (baiduSeoRemain <= 0) {
            return;
        }
        var url = StringUtils.format("https://jiucai.fun/ac/{}", newsId);
        var client = HttpClient.newBuilder().build();
        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        var seoRequest = HttpRequest.newBuilder(URI.create(baiDuAutoSeoApi)).header("Content-Type", "text/plain").POST(HttpRequest.BodyPublishers.ofString(url)).build();
        var baiDuSeoResponse = JsonUtils.string2Object(client.send(seoRequest, responseBodyHandler).body(), BaiDuSeoResponse.class);

        if (!StringUtils.isBlank(baiDuSeoResponse.getMessage())) {
            log.error("推送失败[error:{}][message:{}]", baiDuSeoResponse.getError(), baiDuSeoResponse.getMessage());
            return;
        }

        if (CollectionUtils.isNotEmpty(baiDuSeoResponse.getNotSameSite()) || CollectionUtils.isNotEmpty(baiDuSeoResponse.getNotValid())) {
            log.error("推送url不合法[success:{}][remain:{}][not_same_site:{}][not_valid:{}]", baiDuSeoResponse.getSuccess(), baiDuSeoResponse.getRemain(), baiDuSeoResponse.getNotSameSite(), baiDuSeoResponse.getNotValid());
            return;
        }

        baiduSeoRemain = baiDuSeoResponse.getRemain();
        log.info("百度seo推送[url:{}][success:{}][remain:{}]成功", url, baiDuSeoResponse.getSuccess(), baiDuSeoResponse.getRemain());
    }
}
