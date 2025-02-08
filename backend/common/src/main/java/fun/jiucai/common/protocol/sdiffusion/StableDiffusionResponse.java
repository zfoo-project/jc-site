package fun.jiucai.common.protocol.sdiffusion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class StableDiffusionResponse {

    private List<String> images;
    private StableDiffusionParameters parameters;

}
