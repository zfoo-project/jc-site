package fun.jiucai.common.protocol.animation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimationRequest {

    private String nonce;
    private String imageUrl;
    private String type;
    private String[] prompts;

}
