package fun.jiucai.common.protocol.auth;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class LoginRequest {

    // 最大的新闻Id
    private long newsId;
    // 最大的聊天Id
    private long chatMessageId;

    private String token;

}
