package fun.jiucai.common.protocol.news;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 */
@Data
public class News {
    private long id;

    private String level;

    private String title;

    private String content;

    private String ctime;

    private List<NewsStock> stocks = new ArrayList<>();
    private List<NewsConcept> concepts = new ArrayList<>();
    private List<String> subjects = new ArrayList<>();
}
