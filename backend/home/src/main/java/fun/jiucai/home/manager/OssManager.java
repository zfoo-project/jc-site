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

package fun.jiucai.home.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PolicyConditions;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.protocol.auth.OssPolicyVO;
import fun.jiucai.home.config.MyConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Date;

/**
 * @author godotg
 */
@Slf4j
@Component
public class OssManager implements ApplicationListener<ContextClosedEvent> {


    // -----------------------------------oss配置-----------------------------------------
    @Autowired
    private MyConfiguration myConfiguration;

    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String bucketName;
    private OSS ossClient;

    @PostConstruct
    public void initOss() {
        accessKeyId = myConfiguration.getAliyun().getAccessKeyId();
        accessKeySecret = myConfiguration.getAliyun().getAccessKeySecret();
        endpoint = myConfiguration.getAliyun().getEndpoint();
        bucketName = myConfiguration.getAliyun().getBucketName();
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        ossClient.shutdown();
    }

    public void upload(byte[] bytes, String objectName) {
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
    }

    public void upload(byte[] bytes, String objectName, ObjectMetadata meta) {
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes), meta);
    }

    public void uploadIfAbsent(byte[] bytes, String objectName, ObjectMetadata meta) {
        if (ossClient.doesObjectExist(bucketName, objectName)) {
            return;
        }
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes), meta);
    }

    public void delete(String objectName) {
        ossClient.deleteObject(bucketName, objectName);
    }

    public OssPolicyVO policy() {
        var expireEndTime = TimeUtils.now() + 24 * TimeUtils.MILLIS_PER_SECOND;
        var maxFileSize = 18 * IOUtils.BYTES_PER_MB;

        var expiration = new Date(expireEndTime);
        var policyConditions = new PolicyConditions();

        // 使用uid做前缀，防止互相覆盖文件
        var dir = StringUtils.format("am/{}", RandomUtils.randomString(30));
        policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxFileSize);
        policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        var postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
        var binaryData = StringUtils.bytes(postPolicy);
        var encodedPolicy = Base64.getEncoder().encodeToString(binaryData);
        var postSignature = ossClient.calculatePostSignature(postPolicy);

        var ossPolicy = new OssPolicyVO(encodedPolicy, accessKeyId, postSignature, dir, "https://jiucai.fun", String.valueOf(expireEndTime / 1000));
        return ossPolicy;
    }
}
