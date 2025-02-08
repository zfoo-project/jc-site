package fun.jiucai.cloud.util;

import com.zfoo.protocol.util.StringUtils;

/**
 * @author godotg
 */
public abstract class CopyrightUtils {

    public static String noCopyright(String content) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }
        var str = content;
        if (content.startsWith("财联社")) {
            str = StringUtils.substringAfterFirst(content, "，");
        }
        str =  str.replaceAll("[\\(|（]财联社.*[\\)|）]", "");
        //str = str.replaceAll("习近平", "喜da大");
        str = str.replaceAll("财联社", "快乐韭菜网");
        return str;
    }

}
