package fun.jiucai.common.protocol.sdiffusion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SdImage {

    private long id;
    private String imageUrl;
    private String imageUrlLow;
    private String imageUrlMiddle;
    private String imageUrlHigh;

}
