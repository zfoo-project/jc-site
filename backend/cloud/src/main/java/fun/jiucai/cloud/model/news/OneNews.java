package fun.jiucai.cloud.model.news;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneNews {

    private long id;

    private int type;

    private String level;

    private String title;

    private String content;

    private String img;

    private String shareurl;

    private long ctime;

    private int recommend;

    @JsonProperty("reading_num")
    private int readingNum;

    @JsonProperty("share_num")
    private int shareNum;

    private List<Subject> subjects;

    @JsonProperty("stock_list")
    private List<StockSimpleInfo> stocks;

}
