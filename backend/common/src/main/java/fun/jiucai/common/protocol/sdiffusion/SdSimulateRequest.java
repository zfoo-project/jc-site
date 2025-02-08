package fun.jiucai.common.protocol.sdiffusion;

import com.zfoo.protocol.anno.Note;
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
public class SdSimulateRequest {

    private long nonce;
    private String prompt;
    private String negativePrompt;
    private int steps;
    private int batchSize;

    @Note("图片的风格，0->二次元")
    private int style;
    @Note("图片的尺寸，0->768*768，1->768*1024")
    private int dimension;

    private List<Long> ignores;

}