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
import com.zfoo.net.router.attachment.SignalAttachment;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.SessionUtils;
import com.zfoo.net.util.security.AesUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.LazyCache;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.anno.StorageAutowired;
import com.zfoo.storage.model.IStorage;
import fun.jiucai.cloud.resource.BannedResource;
import fun.jiucai.common.constant.SdDimensionEnum;
import fun.jiucai.common.constant.SdStyleEnum;
import fun.jiucai.cloud.statistic.event.StatMidImagineRequestEvent;
import fun.jiucai.cloud.statistic.event.StatSdSimulateRequestEvent;
import fun.jiucai.common.protocol.auth.OssPolicyRequest;
import fun.jiucai.common.protocol.auth.OssPolicyResponse;
import fun.jiucai.common.protocol.broker.*;
import fun.jiucai.common.protocol.midjourney.*;
import fun.jiucai.common.protocol.sdiffusion.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 */
@Slf4j
@Component
public class HomeMidjourneyController {

    public static final int NONCE_LENGTH = 19;

    public LazyCache<String, MidImagineNotice> imagines = new LazyCache<>(10000, 1 * TimeUtils.MILLIS_PER_DAY, 20 * TimeUtils.MILLIS_PER_MINUTE, null);


    @Autowired
    private BrokerService brokerService;
    @StorageAutowired
    private IStorage<Integer, BannedResource> bannedStorage;


    @PacketReceiver
    public void atMidImagineRequest(Session session, MidImagineRequest request) {
        var nonce = StringUtils.trim(request.getNonce());
        var prompt = StringUtils.trim(request.getPrompt());
        if (StringUtils.isBlank(nonce)) {
            NetContext.getRouter().send(session, Message.valueError("nonce is empty"));
            return;
        }
        if (nonce.length() < NONCE_LENGTH) {
            NetContext.getRouter().send(session, Message.valueError("nonce is illegal"));
            return;
        }
        if (StringUtils.isBlank(prompt)) {
            NetContext.getRouter().send(session, Message.valueError("prompt is empty"));
            return;
        }
        if (!brokerService.existBroker(BrokerRegisterAsk.HOME)) {
            NetContext.getRouter().send(session, Message.valueError("broker is crash"));
            return;
        }
        var lowerPrompt = prompt.toLowerCase();
        if (bannedStorage.getAll().stream().anyMatch(it -> lowerPrompt.contains(it.getWord()))) {
            NetContext.getRouter().send(session, Message.valueError("prompt包含不健康的词语，有概率生成失败"));
        }

        // 防止空格导致md文档格式错误
        request.setNonce(nonce);
        request.setPrompt(prompt);
        brokerService.broker(BrokerRegisterAsk.HOME, new TransferMidImagineAsk(session.getSid(), request));
        EventBus.post(new StatMidImagineRequestEvent(session));
    }

    @PacketReceiver
    public void atMidRerollRequest(Session session, MidRerollRequest request) {
        if (StringUtils.trim(request.getNonce()).length() < NONCE_LENGTH) {
            NetContext.getRouter().send(session, Message.valueError("nonce is illegal"));
            return;
        }
        if (!brokerService.existBroker(BrokerRegisterAsk.HOME)) {
            NetContext.getRouter().send(session, Message.valueError("broker is offline"));
            return;
        }

        brokerService.broker(BrokerRegisterAsk.HOME, new TransferMidRerollAsk(session.getSid(), request));
        EventBus.post(new StatMidImagineRequestEvent(session));
    }

    @PacketReceiver
    public void atMidSelectRequest(Session session, MidSelectRequest request) {
        if (StringUtils.trim(request.getNonce()).length() < NONCE_LENGTH) {
            NetContext.getRouter().send(session, Message.valueError("nonce is illegal"));
            return;
        }
        var category = StringUtils.trim(request.getCategory());
        if (!category.equals("upsample") && !category.equals("variation")) {
            NetContext.getRouter().send(session, Message.valueError("category is illegal"));
            return;
        }
        if (!brokerService.existBroker(BrokerRegisterAsk.HOME)) {
            NetContext.getRouter().send(session, Message.valueError("broker is down"));
            return;
        }

        var index = request.getIndex();
        if (index < 0 || index > 4) {
            NetContext.getRouter().send(session, Message.valueError("index is illegal"));
            return;
        }
        brokerService.broker(BrokerRegisterAsk.HOME, new TransferMidSelectAsk(session.getSid(), request));
        EventBus.post(new StatMidImagineRequestEvent(session));
    }

    @PacketReceiver
    public void atMidUpscaleRequest(Session session, MidUpscaleRequest request) {
        if (StringUtils.trim(request.getNonce()).length() < NONCE_LENGTH) {
            NetContext.getRouter().send(session, Message.valueError("nonce is illegal"));
            return;
        }
        if (!brokerService.existBroker(BrokerRegisterAsk.HOME)) {
            NetContext.getRouter().send(session, Message.valueError("broker is down"));
            return;
        }

        brokerService.broker(BrokerRegisterAsk.HOME, new TransferMidUpscaleAsk(session.getSid(), request));
        EventBus.post(new StatMidImagineRequestEvent(session));
    }

    @PacketReceiver
    public void atMidZoomRequest(Session session, MidZoomRequest request) {
        if (StringUtils.trim(request.getNonce()).length() < NONCE_LENGTH) {
            NetContext.getRouter().send(session, Message.valueError("nonce is illegal"));
            return;
        }
        var zoom = StringUtils.trim(request.getZoom());
        if (!zoom.equals("50") && !zoom.equals("75")) {
            NetContext.getRouter().send(session, Message.valueError("zoom is illegal"));
            return;
        }
        if (!brokerService.existBroker(BrokerRegisterAsk.HOME)) {
            NetContext.getRouter().send(session, Message.valueError("broker is down"));
            return;
        }

        brokerService.broker(BrokerRegisterAsk.HOME, new TransferMidZoomAsk(session.getSid(), request));
        EventBus.post(new StatMidImagineRequestEvent(session));
    }

    @PacketReceiver
    public void atMidInpaintRequest(Session session, MidInpaintRequest request) {
        if (StringUtils.trim(request.getNonce()).length() < NONCE_LENGTH) {
            NetContext.getRouter().send(session, Message.valueError("nonce is illegal"));
            return;
        }
        if (!brokerService.existBroker(BrokerRegisterAsk.HOME)) {
            NetContext.getRouter().send(session, Message.valueError("broker is down"));
            return;
        }

        brokerService.broker(BrokerRegisterAsk.HOME, new TransferMidInpaintAsk(session.getSid(), request));
        EventBus.post(new StatMidImagineRequestEvent(session));
    }

    @PacketReceiver
    public void atTransferMidImagineNotify(Session session, TransferMidImagineNotify notify) {
        var noticeSession = NetContext.getSessionManager().getServerSession(notify.getNoticeSid());
        var imagineNotice = notify.getNotice();
        imagines.put(imagineNotice.getNonce(), imagineNotice);
        NetContext.getRouter().send(noticeSession, imagineNotice);
    }

    @PacketReceiver
    public void atMidHistoryRequest(Session session, MidHistoryRequest request) {
        if (StringUtils.isBlank(request.getNonce())) {
            return;
        }
        var imagineNotice = imagines.get(request.getNonce());
        if (imagineNotice == null) {
            return;
        }
        NetContext.getRouter().send(session, imagineNotice);
    }

    // ----------------------------------------------------------------------------------------------
    public LazyCache<Long, SdSimulateNotice> sdImages = new LazyCache<>(10000, 1 * TimeUtils.MILLIS_PER_DAY, 20 * TimeUtils.MILLIS_PER_MINUTE, null);

    @PacketReceiver
    public void atSdSimulateRequest(Session session, SdSimulateRequest request, SignalAttachment attachment) {
        var nonce = request.getNonce();
        var prompt = StringUtils.trim(request.getPrompt());
        var steps = request.getSteps();
        var batchSize = request.getBatchSize();
        var style = request.getStyle();
        var dimension = request.getDimension();

        if (steps < 20 || steps > 150) {
            NetContext.getRouter().send(session, Message.valueError("steps is illegal"));
            return;
        }
        if (batchSize <= 0) {
            NetContext.getRouter().send(session, Message.valueError("batchSize is illegal"));
            return;
        }
        if (SdStyleEnum.sdStyle(style) == null) {
            NetContext.getRouter().send(session, Message.valueError("style is illegal"));
            return;
        }
        if (SdDimensionEnum.sdDimension(dimension) == null) {
            NetContext.getRouter().send(session, Message.valueError("dimension is illegal"));
            return;
        }
        if (StringUtils.isBlank(prompt)) {
            NetContext.getRouter().send(session, Message.valueError("prompt is empty"));
            return;
        }
        if (!brokerService.existBroker(BrokerRegisterAsk.HOME)) {
            NetContext.getRouter().send(session, Message.valueError("broker is crash"));
            return;
        }

        // 防止空格导致md文档格式错误
        request.setPrompt(prompt);
        brokerService.broker(BrokerRegisterAsk.HOME, new SdSimulateAsk(session.getSid(), request, attachment));
        EventBus.post(new StatSdSimulateRequestEvent(session));
    }

    @PacketReceiver
    public void atSdSimulateAnswer(Session session, SdSimulateAnswer answer) {
        var noticeSession = NetContext.getSessionManager().getServerSession(answer.getNoticeSid());
        var response = answer.getResponse();
        var attachment = answer.getAttachment();
        NetContext.getRouter().send(noticeSession, response, attachment);
    }

    @PacketReceiver
    public void atTransferSdSimulateNotify(Session session, TransferSdSimulateNotify notify) {
        var noticeSession = NetContext.getSessionManager().getServerSession(notify.getNoticeSid());
        var sdNotice = notify.getNotice();
        sdImages.put(sdNotice.getNonce(), sdNotice);
        NetContext.getRouter().send(noticeSession, sdNotice);
    }

    @PacketReceiver
    public void atSdHistoryRequest(Session session, SdHistoryRequest request) {
        var sdNotice = sdImages.get(request.getNonce());
        if (sdNotice == null) {
            return;
        }
        NetContext.getRouter().send(session, sdNotice);
    }

    @Deprecated
    @PacketReceiver
    public void atImageDownloadRequest(Session session, ImageDownloadRequest request) {
        var url = request.getUrl();
        var realUrl = AesUtils.getDecryptString(url);
        NetContext.getRouter().send(session, new ImageDownloadResponse(realUrl));
        brokerService.broker(BrokerRegisterAsk.HOME, new ImageDeleteAsk(realUrl));
    }

    @PacketReceiver
    public void atOssPolicyRequest(Session session, OssPolicyRequest request) {
        var type = request.getType();
        var ip = SessionUtils.toIp(session);
        var sid = session.getSid();
        log.info("oss policy ip:[{}] sid:[{}] type:[{}]", ip, sid, type);

        var brokerSession = brokerService.brokerSession(BrokerRegisterAsk.HOME);
        NetContext.getRouter().asyncAsk(brokerSession, request, OssPolicyResponse.class, sid)
                .whenComplete(response -> NetContext.getRouter().send(session, response));
    }

}
