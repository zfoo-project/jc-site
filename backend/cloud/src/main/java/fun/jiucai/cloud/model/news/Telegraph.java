package fun.jiucai.cloud.model.news;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Telegraph {

    private int error;

    private NewsData data;

}
