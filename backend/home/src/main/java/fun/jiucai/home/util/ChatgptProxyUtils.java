package fun.jiucai.home.util;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.common.util.HttpProxyUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

/**
 * @author godotg
 */
@Slf4j
public abstract class ChatgptProxyUtils {

    /**
     * 使用spring resttemplate提供的http客户端
     */
    public static List<ChatCompletionChoice> chat(List<ChatMessage> messages) {
        var chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(5)
                .maxTokens(50)
                .logitBias(new HashMap<>())
                .build();

        var headers = new HashMap<String, String>();
        headers.put("Authorization", StringUtils.format("Bearer {}", "your-toekn"));

        var response = HttpProxyUtils.post("https://api.openai.com/v1/chat/completions", chatCompletionRequest, headers);
        var result = JsonUtils.string2Object(response, ChatCompletionResult.class);
        log.info("chatgpt --> {}", response);
        return result.getChoices();
    }

}
