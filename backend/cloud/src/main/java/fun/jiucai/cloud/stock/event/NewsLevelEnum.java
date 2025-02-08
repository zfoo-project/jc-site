package fun.jiucai.cloud.stock.event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author godotg
 */
public enum NewsLevelEnum {

    S((byte) 1),
    A((byte) 2),
    B((byte) 3),
    C((byte) 4),
    D((byte) 5);

    private byte type;

    public byte getType() {
        return type;
    }

    NewsLevelEnum(byte type) {
        this.type = type;
    }

    private static Map<String, NewsLevelEnum> nameMap = new HashMap<>();
    private static Map<Byte, NewsLevelEnum> typeMap = new HashMap<>();

    static {
        for (var newsLevel : NewsLevelEnum.values()) {
            nameMap.put(newsLevel.name(), newsLevel);
            typeMap.put(newsLevel.getType(), newsLevel);
        }
    }

    public static NewsLevelEnum newsLevelOfName(String name) {
        return nameMap.get(name);
    }

    public static NewsLevelEnum newsLevelOfType(byte type) {
        var level = typeMap.get(type);
        return level == null ? D : level;
    }
}
