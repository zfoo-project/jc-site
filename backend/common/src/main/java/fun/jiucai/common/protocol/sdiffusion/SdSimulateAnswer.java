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
public class SdSimulateAnswer {

    private long noticeSid;
    private SignalAttachment attachment;

    private SdSimulateResponse response;

}