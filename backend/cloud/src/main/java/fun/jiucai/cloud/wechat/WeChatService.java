package fun.jiucai.cloud.wechat;

import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.SingleCache;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.wechat.model.AccessTokenResponse;
import fun.jiucai.common.util.HttpUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WeChatService {

    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={}&secret={}";

    @Value("${jiucai.weixing.serviceSecurityKey}")
    private String serviceSecurityKey;
    @Value("${jiucai.weixing.appId}")
    private String appId;

    public SingleCache<String> accessTokenCache;

    @PostConstruct
    public void init() {
        accessTokenCache = SingleCache.build(30 * TimeUtils.MILLIS_PER_MINUTE, () -> {
            var url = StringUtils.format(ACCESS_TOKEN_URL, appId, serviceSecurityKey);
            try {
                var jsonResponse = HttpUtils.getNoHeaders(url);
                var response = JsonUtils.string2Object(jsonResponse, AccessTokenResponse.class);
                return response.getAccessToken();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
