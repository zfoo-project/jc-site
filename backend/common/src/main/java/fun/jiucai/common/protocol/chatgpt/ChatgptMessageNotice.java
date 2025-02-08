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
public class ChatgptMessageNotice {

    public static final int GENERATING = 0;
    public static final int STOP = 1;
    public static final int EXCEPTION = 2;

    private long requestId;

    // ChatAIEnum
    private int chatAI;

    private String choice;

    // 0还没有结束，1代表正常结束，2代表非正常结束
    private int finishReason;

}
