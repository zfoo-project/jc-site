package fun.jiucai.common.protocol.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileResponse {

    private User user;

}
