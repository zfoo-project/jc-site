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
public class AdminInfoResponse {

    private List<Broadcast> broadcasts;

    private List<Statistics> stats;

}
