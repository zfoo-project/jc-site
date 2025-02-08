package fun.jiucai.common.protocol.midjourney;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class MidUpscaleRequest {

    private String category;

    private String nonce;

    private long midjourneyId;

}
