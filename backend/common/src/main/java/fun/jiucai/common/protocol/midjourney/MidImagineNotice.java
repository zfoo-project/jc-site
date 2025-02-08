package fun.jiucai.common.protocol.midjourney;

import com.zfoo.protocol.anno.Compatible;
import com.zfoo.protocol.anno.Note;
import lombok.Data;

/**
 * @author godotg
 */
@Data
public class MidImagineNotice {

    public static final String provider = "provider";
    public static final String consumer = "consumer";
    public static final String create = "create";
    public static final String update = "update";
    public static final String complete = "complete";
    public static final String stop = "stop";
    public static final String expire = "expire";

    @Note("provider为加入到了providers队列，consumer为开始消费任务，create为创建消息，update为更新消息，complete为创建完成，stop为发生错误停止生成，expire图片过期")
    private String type;
    private String nonce;
    private String content;

    @Note("type为complete状态才有意义")
    private long midjourneyId;
    @Note("只有type为complete状态才能够访问图片")
    private String imageUrl;
    private String imageUrlLow;
    private String imageUrlMiddle;
    private String imageUrlHigh;

    @Note("只有type为update状态才有意义")
    private int progress;
    @Note("只有type为complete状态才有意义")
    private boolean reroll;
    private boolean upsample;
    private boolean upscale;

    public static MidImagineNotice valueOf(String type, String nonce, String content) {
        var notice = new MidImagineNotice();
        notice.type = type;
        notice.nonce = nonce;
        notice.content = content;
        return notice;
    }

}
