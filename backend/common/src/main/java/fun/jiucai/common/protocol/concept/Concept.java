package fun.jiucai.common.protocol.concept;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class Concept {
    private long id;

    private String level;

    private String title;

    private String content;

    private String url;

    private String ctime;
}
