package fun.jiucai.common.protocol.sdiffusion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferSdSimulateNotify {

    private long noticeSid;
    private SdSimulateNotice notice;

}
