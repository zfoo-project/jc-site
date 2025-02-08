package fun.jiucai.common.protocol.auth;

import fun.jiucai.common.protocol.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String ip;

    private long ipLong;

    private long sid;

    private long activeUid;

    private String region;

    private long chatMessageIdDiff;

    private User user;
}
