package fun.jiucai.cloud.service.mychatgpt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author godotg
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jiucai.chatgpt")
public class MyChatgptConfiguration {

    private List<MyUrlToken> mytokens;

}
