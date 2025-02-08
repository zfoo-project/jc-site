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

import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.cloud.statistic.event.StatAnimationRequestEvent;
import fun.jiucai.common.protocol.animation.AnimationRequest;
import fun.jiucai.common.protocol.animation.TransferAnimationAsk;
import fun.jiucai.common.protocol.animation.TransferAnimationNotify;
import fun.jiucai.common.protocol.broker.BrokerRegisterAsk;
import fun.jiucai.common.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 */
@Slf4j
@Component
public class HomeGpuAnimationController {

    @Autowired
    private BrokerService brokerService;


    @PacketReceiver
    public void atAnimationRequest(Session session, AnimationRequest request) {
        if (StringUtils.isBlank(request.getNonce())) {
            NetContext.getRouter().send(session, Message.valueError("requestId is empty"));
            return;
        }
//        if (ArrayUtils.isEmpty(request.getPrompts())) {
//            NetContext.getRouter().send(session, Message.valueError("prompts is empty"));
//            return;
//        }
        if (!HttpUtils.isHttpUrl(request.getImageUrl())) {
            NetContext.getRouter().send(session, Message.valueError("imageUrl is not http"));
            return;
        }

        EventBus.post(new StatAnimationRequestEvent(session));
        brokerService.broker(BrokerRegisterAsk.HOME_GPU, new TransferAnimationAsk(session.getSid(), request));
    }

    @PacketReceiver
    public void atTransferAnimationNotify(Session session, TransferAnimationNotify notify) {
        var noticeSession = NetContext.getSessionManager().getServerSession(notify.getRequestSid());
        if (noticeSession == null) {
            return;
        }
        var notice = notify.getNotice();
        NetContext.getRouter().send(noticeSession, notice);
    }

}
