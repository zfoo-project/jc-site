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

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.common.constant.ChatAIEnum;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessageNotice;
import fun.jiucai.common.protocol.chatgpt.TransferChatgptNotify;
import fun.jiucai.home.config.MyConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
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
public class BaiduService {

    @Autowired
    private MyConfiguration myConfiguration;

    private List<Qianfan> qianfans = new ArrayList<>();

    private AtomicInteger atomic = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        // 创建一个本地的 Cookie 存储
        final CookieStore cookieStore = new BasicCookieStore();
         BasicClientCookie clientCookie = new BasicClientCookie("name", "aip.baidubce.com");
         clientCookie.setDomain("aip.baidubce.com");
        // 过期时间
        // 添加到本地 Cookie
         cookieStore.addCookie(clientCookie);

        // 创建本地 HTTP 请求上下文 HttpClientContext
        final HttpClientContext localContext = HttpClientContext.create();
        // 绑定 cookieStore 到 localContext
        localContext.setCookieStore(cookieStore);
        for (var config : myConfiguration.getBaidu()) {
            var qianfan = new Qianfan(Auth.TYPE_OAUTH, config.getAccessKey(), config.getSecretKey());
            qianfans.add(qianfan);
        }

    }

    public void process(Session session, long requestSid, long requestId, List<ChatgptMessage> messages) {
        var qianfan = qianfans.get(Math.abs(atomic.incrementAndGet() % qianfans.size()));
        // ERNIE-Lite-8K 同样是免费的
        var chatBuilder = qianfan.chatCompletion().model("ERNIE-Speed-8K");

        for (var message : messages) {
            if (message.getRole().equals("user")) {
                chatBuilder.addUserMessage(message.getContent());
            } else {
                chatBuilder.addAssistantMessage(message.getContent());
            }
        }

        EventBus.asyncExecute(qianfan.hashCode(), () -> {
            chatBuilder.executeStream()
                    .forEachRemaining(chunk -> {
                        var chatRequestId = Integer.parseInt(StringUtils.format("{}{}", ChatAIEnum.baidu.getType(), requestId));
                        var finishReason = chunk.getEnd() ? ChatgptMessageNotice.STOP : ChatgptMessageNotice.GENERATING;
                        var notice = new ChatgptMessageNotice(chatRequestId, ChatAIEnum.baidu.getType(), chunk.getResult(), finishReason);
                        var notify = new TransferChatgptNotify(requestSid, notice);
                        NetContext.getRouter().send(session, notify);
                    });
        });
    }
}
