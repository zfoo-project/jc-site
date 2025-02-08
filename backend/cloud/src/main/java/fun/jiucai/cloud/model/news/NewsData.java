package fun.jiucai.cloud.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsData {

    @JsonProperty("roll_data")
    private List<OneNews> rollData;

    @JsonProperty("update_num")
    private int updateNum;

}
