package fun.jiucai.common.protocol.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private long id;

    private byte type;
    /**
     * 发送者的id
     */
    private long sendId;

    /**
     * 地域
     */
    private String region;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 发送的时间戳
     */
    private long timestamp;

}
