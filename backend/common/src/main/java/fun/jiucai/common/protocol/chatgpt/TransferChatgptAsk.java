package fun.jiucai.common.protocol.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferChatgptAsk {

    private long requestSid;
    private long requestId;
    private int chatAI;
    private List<ChatgptMessage> messages;
    private Set<Integer> ignoreAIs;

}
