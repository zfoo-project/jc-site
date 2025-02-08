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

import com.zfoo.event.manager.EventBus;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.util.RandomUtils;
import fun.jiucai.common.constant.ChatAIEnum;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import fun.jiucai.common.protocol.chatgpt.TransferChatgptAsk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ChatAIController {

    @Autowired
    private BaiduService baiduService;
    @Autowired
    private XunfeiService xunfeiService;
    @Autowired
    private TencentService tencentService;
    @Autowired
    private GoogleService googleService;
    @Autowired
    private QwenService qwenService;
    @Autowired
    private LlamaService llamaService;
    @Autowired
    private DeepSeekService deepSeekService;

    @PacketReceiver
    public void atTransferChatgptAsk(Session session, TransferChatgptAsk ask) {
        var requestSid = ask.getRequestSid();
        var requestId = ask.getRequestId();
        var chatAI = ask.getChatAI();
        var ignoreAIs = ask.getIgnoreAIs();
        var messages = ask.getMessages();
        log.info("atTransferChatgptAsk requestSid:[{}] requestId:[{}] chatAI:[{}] messages:[{}] ignoreAIs:[{}]", requestSid, requestId, ChatAIEnum.typeOf(chatAI), messages.size(), ignoreAIs);
        var noSystemMessages = noSystemMessages(messages);
        var random = RandomUtils.randomInt();
        if (!ignoreAIs.contains(ChatAIEnum.xunfei.getType())) {
            EventBus.asyncExecute(random++, () -> xunfeiService.process(session, requestSid, requestId, noSystemMessages));
        }

        if (!ignoreAIs.contains(ChatAIEnum.baidu.getType())) {
            EventBus.asyncExecute(random++,() -> baiduService.process(session, requestSid, requestId, noSystemMessages));
        }

        if (!ignoreAIs.contains(ChatAIEnum.tencent.getType())) {
            EventBus.asyncExecute(random++,() -> tencentService.process(session, requestSid, requestId, noSystemMessages));
        }

        if (!ignoreAIs.contains(ChatAIEnum.alibaba.getType())) {
            EventBus.asyncExecute(random++,() -> qwenService.process(session, requestSid, requestId, noSystemMessages));
        }

        if (!ignoreAIs.contains(ChatAIEnum.llama.getType())) {
            EventBus.asyncExecute(random++,() -> llamaService.process(session, requestSid, requestId, noSystemMessages));
        }

        if (!ignoreAIs.contains(ChatAIEnum.google.getType())) {
            EventBus.asyncExecute(random++,() -> googleService.process(session, requestSid, requestId, noSystemMessages));
        }

        if (!ignoreAIs.contains(ChatAIEnum.deepseek.getType())) {
            EventBus.asyncExecute(random++,() -> deepSeekService.process(session, requestSid, requestId, noSystemMessages));
        }
    }

    public List<ChatgptMessage> noSystemMessages(List<ChatgptMessage> messages) {
        var newMessages = new ArrayList<ChatgptMessage>();

        ChatgptMessage lastUserMessage = null;
        for (var message : messages) {
            if (message.getRole().equals("user")) {
                newMessages.add(message);
                lastUserMessage = message;
            } else if (message.getRole().equals("assistant")) {
                newMessages.add(message);
            }
        }

        for (var message : messages) {
            if (message.getRole().equals("system")) {
                lastUserMessage.setContent(message.getContent() + "。 " + lastUserMessage.getContent());
            }
        }

        return newMessages;
    }

}
