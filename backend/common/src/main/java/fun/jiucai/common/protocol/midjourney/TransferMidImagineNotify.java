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
public class TransferMidImagineNotify {

    private long noticeSid;
    private MidImagineNotice notice;

}
