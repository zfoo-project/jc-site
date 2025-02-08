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
public class SdSimulateResponse {

    private long nonce;
    private long costTime;
    private String enPrompt;

}