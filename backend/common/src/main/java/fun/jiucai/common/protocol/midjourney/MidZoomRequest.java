package fun.jiucai.common.protocol.midjourney;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class MidZoomRequest {

    private String zoom;

    private String nonce;

    private long midjourneyId;

}
