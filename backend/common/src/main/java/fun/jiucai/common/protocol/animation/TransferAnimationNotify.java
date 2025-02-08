package fun.jiucai.common.protocol.animation;

import fun.jiucai.common.protocol.chatgpt.ChatgptMessageNotice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferAnimationNotify {

    private long requestSid;

    private AnimationNotice notice;

}
