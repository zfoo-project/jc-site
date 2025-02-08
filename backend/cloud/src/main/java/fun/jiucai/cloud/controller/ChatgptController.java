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

package fun.jiucai.cloud.controller;

import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.SessionUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.model.chat.MessageData;
import fun.jiucai.cloud.model.event.ChatgptEvent;
import fun.jiucai.cloud.resource.ChatNoticeResource;
import fun.jiucai.cloud.service.ChatgptService;
import fun.jiucai.common.protocol.chatgpt.ChatgptForceStopRequest;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ChatgptController {

    @Autowired
    private ChatgptService chatgptService;
    private Set<String> roles = Set.of("user", "assistant", "system");

    private LinkedBlockingQueue<MessageData> messageQueue = new LinkedBlockingQueue<>(200);


    @PacketReceiver
    public void atChatgptMessageRequest(Session session, ChatgptMessageRequest request) {
        var messages = request.getMessages()
                .stream()
                .filter(it -> roles.contains(it.getRole()))
                .filter(it -> StringUtils.isNotBlank(it.getContent()))
                .map(it -> new ChatgptMessage(it.getRole(), StringUtils.trim(it.getContent())))
                .toList();
        if (CollectionUtils.isEmpty(messages)) {
            NetContext.getRouter().send(session, Message.valueError("prompt不能为空"));
            return;
        }
        if (messages.size() > 4) {
            NetContext.getRouter().send(session, Message.valueError("最多支持4条对话"));
            return;
        }
        if (messages.size() == 4 && messages.stream().noneMatch(it -> it.getRole().equals("system"))) {
            NetContext.getRouter().send(session, Message.valueError("最多支持3条对话和一条system对话"));
            return;
        }
        var lastMessage = messages.get(messages.size() - 1);
        if (!lastMessage.getRole().equals("user")) {
            NetContext.getRouter().send(session, Message.valueError("需要user发起最后一条对话"));
            return;
        }
//        if (session.getUid() <= 0) {
//            NetContext.getRouter().send(session, Message.valueError("请登录微信"));
//            return;
//        }

        var contentTotalSize = messages.stream().mapToInt(it -> it.getContent().length()).count();
        log.info("talk [sid:{}] [ignoreAi:{}] [length:{}] [size:{}] [{}]", session.getSid(), request.getIgnoreAIs(), messages.size(), contentTotalSize, messages.get(messages.size() - 1));

        // 减少token的使用量，获取最近3条的数据
        request.setMessages(messages);

        var messageData = MessageData.valueOf(session.getSid(), request);
        var isOffered = messageQueue.offer(messageData);

        EventBus.post(new ChatgptEvent(session, session.getSid(), request.getRequestId()
                , messages, request.getIgnoreAIs(), request.isGoogleSearch(), request.isBingSearch(), request.isWeixinSearch(), request.isBilibiliSearch()));

        if (isOffered) {
            NetContext.getRouter().send(session, Message.valueSuccess(waitInfo(messageQueue.size() - 1)));
        } else {
            NetContext.getRouter().send(session, Message.valueError("服务器压力过大，请稍后再试"));
        }
    }


    @PacketReceiver
    public void atChatgptForceStopRequest(Session session, ChatgptForceStopRequest request) {
        var sid = session.getSid();
        var requestId = request.getRequestId();
        var requestUUID = chatgptService.requestUUID(sid, requestId);

        var messageData = chatgptService.processCache.get(requestUUID);
        if (messageData != null) {
            messageData.setForceStop(true);
        }

        messageQueue.stream().filter(it -> it.getSid() == sid && it.getRequest().getRequestId() == requestId).forEach(it -> it.setForceStop(true));
    }

    @Scheduler(cron = "0/1 * * * * ?")
    public void cronProcess() {
        if (messageQueue.isEmpty()) {
            return;
        }
        var first = messageQueue.poll();

        // 开始处理
        first.setProcessTime(TimeUtils.now());

        var session = NetContext.getSessionManager().getServerSession(first.getSid());
        if (!SessionUtils.isActive(session)) {
            return;
        }
        if (first.isForceStop()) {
            return;
        }
        EventBus.asyncExecute(() -> chatgptService.process(first));
    }

    @Scheduler(cron = "0/10 * * * * ?")
    public void cronWait() {
        if (CollectionUtils.isEmpty(messageQueue)) {
            return;
        }

        for (var message : messageQueue) {
            var sid = message.getSid();
            var session = NetContext.getSessionManager().getServerSession(sid);
            if (!SessionUtils.isActive(session)) {
                continue;
            }
            if (message.getRequest().isMobile()) {
                NetContext.getRouter().send(session, Message.valueInfo(chatgptService.randomChatNotice(ChatNoticeResource.MOBILE_WAITING)));
            } else {
                NetContext.getRouter().send(session, Message.valueInfo(chatgptService.randomChatNotice(ChatNoticeResource.PC_WAITING)));
            }
        }
    }

    public String waitInfo(int diff) {
        if (diff <= 3) {
            return StringUtils.format("AI思考中");
        } else {
            return StringUtils.format("前方等待[{}]人，预计等待[{}]", diff, diff * 7);
        }
    }

}
