package fun.jiucai.home.chatai;

import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.common.util.HttpProxyUtils;
import fun.jiucai.home.config.MyConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ChatgptOpenAiService {

    public static final int DEFAULT_TIMEOUT = 120;

    @Autowired
    private MyConfiguration myConfiguration;

    private List<OpenAiService> openais = new ArrayList<>();


    @PostConstruct
    public void init() {
        for (var config : myConfiguration.getOpenai()) {
            var client = OpenAiService.defaultClient(config.getToken(), Duration.ofSeconds(DEFAULT_TIMEOUT))
                    .newBuilder()
                    .proxy(HttpProxyUtils.PROXY_SOCKET)
                    .build();
            var retrofit = OpenAiService.defaultRetrofit(client, JsonUtils.MAPPER);
            var api = retrofit.create(OpenAiApi.class);
            openais.add(new OpenAiService(api));
        }
    }


    public OpenAiService service() {
        return RandomUtils.randomEle(openais);
    }

    public String choiceWithRetry(String prompt, String content, int retryTimes) {
        for (var i = 0; i < retryTimes; i++) {
            try {
                return choice(prompt, content);
            } catch (Exception e) {
                log.error("openai未知异常，重试[{}]", i, e);
            }
        }
        return content;
    }


    public String choice(String prompt, String content) {
        var chatMessages = new ArrayList<ChatMessage>();
        var promptMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt);
        var contentMessage = new ChatMessage(ChatMessageRole.USER.value(), content);
        chatMessages.add(promptMessage);
        chatMessages.add(contentMessage);

        var chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(chatMessages)
                .n(1)
                .build();

        var service = service();
        var chatCompletion = service.createChatCompletion(chatCompletionRequest);

        var choices = chatCompletion.getChoices();
        if (CollectionUtils.isEmpty(choices)) {
            return StringUtils.EMPTY;
        }

        var result = choices.stream()
                .map(it -> it.getMessage().getContent())
                .filter(it -> StringUtils.isNotBlank(it))
                .findFirst()
                .orElse(StringUtils.EMPTY);

        return result;
    }
}
