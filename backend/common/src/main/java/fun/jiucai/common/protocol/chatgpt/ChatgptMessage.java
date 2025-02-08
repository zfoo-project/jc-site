package fun.jiucai.common.protocol.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatgptMessage {

    private String role;
    private String content;

}
