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
public class TransferMidImagineAsk {

    private long requestSid;

    private MidImagineRequest request;

}
