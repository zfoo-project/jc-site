package fun.jiucai.common.protocol.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Broadcast {

    private long id;

    private String content;

    private String weChatResult;

    private String smsResult;

}
