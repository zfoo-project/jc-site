package fun.jiucai.common.protocol.animation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimationNotice {

    private String nonce;
    private String originImageUrl;
    private String originImageUrlCompression;
    private List<AnimationImage> images;
    private String type;

}
