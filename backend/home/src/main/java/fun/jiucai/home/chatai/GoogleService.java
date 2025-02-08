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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zfoo.net.NetContext;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.common.constant.ChatAIEnum;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessageNotice;
import fun.jiucai.common.protocol.chatgpt.TransferChatgptNotify;
import fun.jiucai.home.config.MyConfiguration;
import io.reactivex.Flowable;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.vacuity.ai.sdk.gemini.GeminiClient;
import me.vacuity.ai.sdk.gemini.api.GeminiApi;
import me.vacuity.ai.sdk.gemini.entity.ChatMessage;
import me.vacuity.ai.sdk.gemini.request.ChatRequest;
import me.vacuity.ai.sdk.gemini.response.StreamChatResponse;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.vacuity.ai.sdk.gemini.GeminiClient.*;

/**
 * @author godotg
 */
@Slf4j
@Component
public class GoogleService {

    @Autowired
    private MyConfiguration myConfiguration;

    private List<GeminiClient> googles = new ArrayList<>();

    private AtomicInteger atomic = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        for (var config : myConfiguration.getGoogle()) {
            ObjectMapper mapper = defaultObjectMapper();
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));
            OkHttpClient httpClient = defaultClient(Duration.ofSeconds(60))
                    .newBuilder()
                    .proxy(proxy)
                    .build();
            Retrofit retrofit = defaultRetrofit(httpClient, mapper, null);
            GeminiApi api = retrofit.create(GeminiApi.class);
            GeminiClient client = new GeminiClient(config.getApiKey(), api);
            googles.add(client);
        }

    }

    @SneakyThrows
    public void process(Session session, long requestSid, long requestId, List<ChatgptMessage> messages) {
        var client = googles.get(Math.abs(atomic.incrementAndGet() % googles.size()));

        List<ChatMessage> messageList = new ArrayList<>();
        for (var message : messages) {
            var role = "user".equalsIgnoreCase(message.getRole()) ? "user" : "model";
            var msg = new ChatMessage(role, message.getContent());
            messageList.add(msg);
        }

        ChatRequest request = ChatRequest.builder()
                // set model here, if not set the default is gemini-pro
                .model("gemini-1.5-pro")
                .contents(messageList)
                .build();

        Flowable<StreamChatResponse> response = client.streamChat(request);
        response.doOnNext(s -> {
            var content = StringUtils.EMPTY;
            var finishReason = ChatgptMessageNotice.GENERATING;
            if (s.getUsageMetadata() == null) {
                content = s.getText();
            } else {
                finishReason = ChatgptMessageNotice.STOP;
            }
            var chatRequestId = Integer.parseInt(StringUtils.format("{}{}", ChatAIEnum.google.getType(), requestId));
            var notice = new ChatgptMessageNotice(chatRequestId, ChatAIEnum.google.getType(), content, finishReason);
            var notify = new TransferChatgptNotify(requestSid, notice);
            NetContext.getRouter().send(session, notify);
        }).blockingSubscribe();
    }
}
