package fun.jiucai.common.protocol.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginByWeChatResponse {
    private String authUrl;
}
