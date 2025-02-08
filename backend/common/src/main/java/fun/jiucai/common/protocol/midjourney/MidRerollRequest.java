package fun.jiucai.common.protocol.midjourney;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class MidRerollRequest {

    private String nonce;

    private long midjourneyId;

}
