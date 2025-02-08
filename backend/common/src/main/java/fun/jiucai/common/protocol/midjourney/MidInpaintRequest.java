package fun.jiucai.common.protocol.midjourney;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class MidInpaintRequest {

    private String nonce;

    private long midjourneyId;

}
