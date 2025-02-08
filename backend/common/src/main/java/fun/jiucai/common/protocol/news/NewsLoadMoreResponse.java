package fun.jiucai.common.protocol.news;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsLoadMoreResponse {

    private long startId;
    private List<News> news;

}
