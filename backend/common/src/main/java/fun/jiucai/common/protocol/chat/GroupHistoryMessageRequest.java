package fun.jiucai.common.protocol.chat;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class GroupHistoryMessageRequest {

    private long groupId;

    /**
     * 最老消息的id
     */
    private long lastMessageId;

}
