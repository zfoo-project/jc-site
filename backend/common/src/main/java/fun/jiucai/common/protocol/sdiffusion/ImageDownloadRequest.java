package fun.jiucai.common.protocol.sdiffusion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Deprecated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDownloadRequest {

    private String url;

}