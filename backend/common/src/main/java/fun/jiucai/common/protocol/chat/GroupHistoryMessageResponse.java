
package fun.jiucai.common.protocol.chat;

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
public class GroupHistoryMessageResponse {

    private long groupId;

    private List<ChatMessage> messages;

    private int onlineUsers;

}
