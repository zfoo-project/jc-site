
package fun.jiucai.cloud.model.chat;

import com.zfoo.protocol.util.AssertionUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum MessageEnum {
    /**
     * 文本
     */
    TEXT((byte) 0),
    /**
     * 语音
     */
    AUDIO((byte) 1),
    /**
     * 图片
     */
    IMAGE((byte) 2),
    /**
     * 视频
     */
    VIDEO((byte) 3),
    /**
     * 除了图片和视频的其它文件
     */
    FILE((byte) 4),

    /**
     * 自带表情
     */
    EMOTION((byte) 7),

    /**
     * 系统自带表情
     */
    GIF((byte) 8),

    /**
     * 位置
     */
    LOCATION((byte) 10),
    /**
     * 玩家名片
     */
    USER_PROFILE((byte) 20),
    ;

    private static Map<Byte, MessageEnum> typeMap = new HashMap<>();

    static {
        for (var messageEnum : MessageEnum.values()) {
            var previousValue = typeMap.putIfAbsent(messageEnum.type, messageEnum);
            AssertionUtils.isNull(previousValue, "[{}]中不应该含有重复的枚举类[{}]和[{}]", messageEnum.getClass().getSimpleName(), messageEnum, previousValue);
        }
    }

    private byte type;

    MessageEnum(byte type) {
        this.type = type;
    }

    @Nullable
    public static MessageEnum getMessageEnumByType(byte type) {
        return typeMap.get(type);
    }

    public byte getType() {
        return type;
    }

}
