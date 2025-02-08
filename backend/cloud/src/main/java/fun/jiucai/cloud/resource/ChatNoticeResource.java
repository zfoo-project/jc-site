package fun.jiucai.cloud.resource;

import com.zfoo.storage.anno.Id;
import com.zfoo.storage.anno.Index;
import com.zfoo.storage.anno.Storage;
import lombok.Getter;

@Getter
@Storage
public class ChatNoticeResource {

    public static final int SEVER_TIMEOUT = 1;
    public static final int PC_WAITING = 2;
    public static final int MOBILE_WAITING = 3;

    @Id
    private int id;

    @Index(unique = false)
    private int type;

    private String word;

    @Override
    public String toString() {
        return word;
    }

}
