package fun.jiucai.home.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author godotg
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "myconfiguration")
public class MyConfiguration {

    private List<XunfeiConfig> xunfei;

    private List<BaiduConfig> baidu;

    private List<TencentConfig> tencent;

    private List<GoogleConfig> google;

    private List<SiliconConfig> silicon;

    private List<DeepSeekConfig> deepseek;

    private List<OpenAiConfig> openai;

    private MidjourneyConfig midjourney;

    private AliyunConfig aliyun;


    @Data
    public static class XunfeiConfig {
        private String appId;
        private String apiKey;
        private String apiSecret;
    }

    @Data
    public static class BaiduConfig {
        private String accessKey;
        private String secretKey;
    }

    @Data
    public static class TencentConfig {
        private String secretId;
        private String secretKey;
    }

    @Data
    public static class GoogleConfig {
        private String apiKey;
    }

    @Data
    public static class SiliconConfig {
        private String token;
    }

    @Data
    public static class OpenAiConfig {
        private String token;
    }

    @Data
    public static class DeepSeekConfig {
        private String token;
    }

    @Data
    public static class MidjourneyConfig {
        private String guildId;
        private String channelId;
        private String botToken;
        private String sessionId;
        private String userToken;
    }

    @Data
    public static class AliyunConfig {
        private String accessKeyId;
        private String accessKeySecret;
        private String endpoint;
        private String bucketName;
    }

}
