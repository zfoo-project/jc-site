package fun.jiucai.common.protocol.sdiffusion;

import com.zfoo.net.router.attachment.SignalAttachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SdSimulateAsk {

    private long requestSid;

    private SdSimulateRequest request;

    private SignalAttachment attachment;

}
