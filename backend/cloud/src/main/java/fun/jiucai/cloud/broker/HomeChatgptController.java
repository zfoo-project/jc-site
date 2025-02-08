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

package fun.jiucai.cloud.broker;

import com.zfoo.event.anno.EventReceiver;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import fun.jiucai.cloud.model.event.ChatgptEvent;
import fun.jiucai.common.constant.ChatAIEnum;
import fun.jiucai.common.protocol.broker.BrokerRegisterAsk;
import fun.jiucai.common.protocol.chatgpt.TransferChatgptAsk;
import fun.jiucai.common.protocol.chatgpt.TransferChatgptNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 */
@Slf4j
@Component
public class HomeChatgptController {

    @Autowired
    private BrokerService brokerService;

    @EventReceiver
    public void onChatgptEvent(ChatgptEvent event) {
        if (!brokerService.existBroker(BrokerRegisterAsk.HOME)) {
            return;
        }
        var requestSid = event.getRequestSid();
        var requestId = event.getRequestId();
        var messages = event.getMessages();
        var ignoreAIs = event.getIgnoreAIs();
        var chatAI = ChatAIEnum.all.getType();
        brokerService.broker(BrokerRegisterAsk.HOME, new TransferChatgptAsk(requestSid, requestId, chatAI, messages, ignoreAIs));
    }

    @PacketReceiver
    public void atTransferChatgptNotify(Session session, TransferChatgptNotify notify) {
        var noticeSession = NetContext.getSessionManager().getServerSession(notify.getRequestSid());
        if (noticeSession == null) {
            return;
        }
        var notice = notify.getNotice();
        NetContext.getRouter().send(noticeSession, notice);
    }

}
