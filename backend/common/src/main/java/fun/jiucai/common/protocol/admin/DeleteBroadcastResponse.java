package fun.jiucai.common.protocol.admin;

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
public class DeleteBroadcastResponse {
    private List<Broadcast> broadcasts;
}
