/*
 * Copyright (C) 2020 The zfoo Authors
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package fun.jiucai.home.chatai;

import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.zfoo.net.NetContext;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.common.constant.ChatAIEnum;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessageNotice;
import fun.jiucai.common.protocol.chatgpt.TransferChatgptNotify;
import fun.jiucai.home.chatai.silicon.MyLlamaOpenAi;
import fun.jiucai.home.config.MyConfiguration;
import io.reactivex.functions.Consumer;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author godotg
 */
@Slf4j
@Component
public class LlamaService {

    @Autowired
    private MyConfiguration myConfiguration;

    private List<MyLlamaOpenAi> openais = new ArrayList<>();

    private AtomicInteger atomic = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        for (var config : myConfiguration.getSilicon()) {
            var service = new MyLlamaOpenAi("https://api.siliconflow.cn", config.getToken(), Duration.ofSeconds(30));
            openais.add(service);
        }

    }

    @SneakyThrows
    public void process(Session session, long requestSid, long requestId, List<ChatgptMessage> messages) {
        var service = openais.get(Math.abs(atomic.incrementAndGet() % openais.size()));

        List<ChatMessage> messageList = new ArrayList<>();
        for (var message : messages) {
            var msg = new ChatMessage(message.getRole(), message.getContent());
            messageList.add(msg);
        }

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("THUDM/glm-4-9b-chat")
                .messages(messageList)
                .n(1)
                .logitBias(new HashMap<>())
                .stream(true)
                .build();

        service.streamChatCompletion(chatCompletionRequest).blockingForEach(new Consumer<ChatCompletionChunk>() {
            @Override
            public void accept(ChatCompletionChunk chatCompletionChunk) throws Exception {
                var choices = chatCompletionChunk.getChoices();
                if (CollectionUtils.isEmpty(choices)) {
                    return;
                }
                var choice = choices.get(0);
                var chatMessage = choice.getMessage();
                if (chatMessage == null) {
                    return;
                }
                var content = chatMessage.getContent();
                if (StringUtils.isEmpty(content)) {
                    return;
                }
                var chatRequestId = Integer.parseInt(StringUtils.format("{}{}", ChatAIEnum.llama.getType(), requestId));
                var finishReason = "stop".equalsIgnoreCase(choice.getFinishReason()) ? ChatgptMessageNotice.STOP : ChatgptMessageNotice.GENERATING;
                var notice = new ChatgptMessageNotice(chatRequestId, ChatAIEnum.llama.getType(), content, finishReason);
                var notify = new TransferChatgptNotify(requestSid, notice);
                NetContext.getRouter().send(session, notify);
            }
        });
    }

}
