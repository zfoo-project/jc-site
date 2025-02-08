package fun.jiucai.common.protocol.midjourney;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferMidUpscaleAsk {

    private long requestSid;

    private MidUpscaleRequest request;

}
