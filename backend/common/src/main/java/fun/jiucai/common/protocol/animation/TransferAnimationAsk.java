package fun.jiucai.common.protocol.animation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferAnimationAsk {

    private long requestSid;
    private AnimationRequest request;

}
