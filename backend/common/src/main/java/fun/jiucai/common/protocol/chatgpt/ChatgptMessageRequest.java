package fun.jiucai.common.protocol.chatgpt;

import com.zfoo.protocol.anno.Compatible;
import com.zfoo.protocol.anno.Note;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author godotg
 */
@Data
public class ChatgptMessageRequest {


    private long requestId;

    @Deprecated
    private int ai;

    private boolean mobile;

    private List<ChatgptMessage> messages;

    @Note("不需要哪些AI")
    private Set<Integer> ignoreAIs;

    @Note("google联网搜索")
    private boolean googleSearch;

    @Note("bing联网搜索")
    private boolean bingSearch;

    @Note("bilibili联网搜索")
    private boolean bilibiliSearch;

    @Note("微信联网搜索")
    private boolean weixinSearch;

}
