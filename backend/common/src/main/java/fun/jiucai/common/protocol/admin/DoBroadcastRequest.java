package fun.jiucai.common.protocol.admin;

import com.zfoo.protocol.anno.Note;
import lombok.Data;

/**
 * @author godotg
 */
@Data
public class DoBroadcastRequest {
    private long id;
    @Note("sms代表短信通知，wechat代表微信通知")
    private String type;
}
