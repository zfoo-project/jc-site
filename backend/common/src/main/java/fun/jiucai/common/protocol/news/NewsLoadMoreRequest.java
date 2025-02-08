package fun.jiucai.common.protocol.news;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class NewsLoadMoreRequest {

    private String query;
    private long startId;
    private int level;

}
