package fun.jiucai.common.protocol.admin;

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
public class DoBroadcastResponse {
    private List<Broadcast> broadcasts;
}
