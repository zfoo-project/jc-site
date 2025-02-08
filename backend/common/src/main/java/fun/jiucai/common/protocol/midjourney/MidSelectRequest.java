package fun.jiucai.common.protocol.midjourney;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class MidSelectRequest {

    private String category;

    // 从1开始
    private int index;

    private String nonce;

    private long midjourneyId;

}
