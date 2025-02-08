package fun.jiucai.common.protocol.chat;

import com.zfoo.protocol.anno.Note;
import lombok.Data;

/**
 * @author godotg
 */
@Data
public class GroupChatRequest {

    private long groupId;

    @Note("0为普通聊天，1为Stable Diffusion，2为Midjourney")
    private byte type;

    private String message;

}
