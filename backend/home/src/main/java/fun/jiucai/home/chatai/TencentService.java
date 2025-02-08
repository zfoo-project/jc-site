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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.SSEResponseModel;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsRequest;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsResponse;
import com.tencentcloudapi.hunyuan.v20230901.models.Choice;
import com.tencentcloudapi.hunyuan.v20230901.models.Message;
import com.zfoo.net.NetContext;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.common.constant.ChatAIEnum;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessageNotice;
import fun.jiucai.common.protocol.chatgpt.TransferChatgptNotify;
import fun.jiucai.home.config.MyConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author godotg
 */
@Slf4j
@Component
public class TencentService {

    @Autowired
    private MyConfiguration myConfiguration;

    private List<HunyuanClient> hunyuans = new ArrayList<>();

    private AtomicInteger atomic = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        for (var config : myConfiguration.getTencent()) {
            Credential cred = new Credential(config.getSecretId(), config.getSecretKey());
            ClientProfile clientProfile = new ClientProfile();
            HunyuanClient client = new HunyuanClient(cred, "ap-guangzhou", clientProfile);
            hunyuans.add(client);
        }

    }

    @SneakyThrows
    public void process(Session session, long requestSid, long requestId, List<ChatgptMessage> messages) {
        var client = hunyuans.get(Math.abs(atomic.incrementAndGet() % hunyuans.size()));

        ChatCompletionsRequest req = new ChatCompletionsRequest();
        req.setModel("hunyuan-lite");

        var messageList = new ArrayList<Message>();
        for (var message : messages) {
            var msg = new Message();
            msg.setRole(message.getRole());
            msg.setContent(message.getContent());
            messageList.add(msg);
        }
        req.setMessages(ArrayUtils.listToArray(messageList, Message.class));

        // hunyuan ChatCompletions 同时支持 stream 和非 stream 的情况
        req.setStream(true);
        ChatCompletionsResponse resp = client.ChatCompletions(req);

        // stream 示例
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        for (SSEResponseModel.SSE e : resp) {
            ChatCompletionsResponse eventModel = gson.fromJson(e.Data, ChatCompletionsResponse.class);
            Choice[] choices = eventModel.getChoices();
            if (ArrayUtils.isNotEmpty(choices)) {
                for(var choice : choices) {
                    var chatRequestId = Integer.parseInt(StringUtils.format("{}{}", ChatAIEnum.tencent.getType(), requestId));
                    var finishReason = "stop".equalsIgnoreCase(choice.getFinishReason()) ? ChatgptMessageNotice.STOP : ChatgptMessageNotice.GENERATING;
                    var notice = new ChatgptMessageNotice(chatRequestId, ChatAIEnum.tencent.getType(), choice.getDelta().getContent(), finishReason);
                    var notify = new TransferChatgptNotify(requestSid, notice);
                    NetContext.getRouter().send(session, notify);
                }
            }
        }
    }
}
