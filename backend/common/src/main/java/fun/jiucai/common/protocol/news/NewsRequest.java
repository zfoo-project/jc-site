package fun.jiucai.common.protocol.news;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class NewsRequest {

    private String query;
    private long endId;
    private int level;

}
