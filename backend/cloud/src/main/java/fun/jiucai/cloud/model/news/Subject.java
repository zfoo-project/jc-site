package fun.jiucai.cloud.model.news;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subject {

    @JsonProperty("subject_id")
    private long subjectId;

    @JsonProperty("subject_name")
    private String subjectName;

}
