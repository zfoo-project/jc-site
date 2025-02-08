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

import com.zfoo.event.anno.EventReceiver;
import com.zfoo.net.NetContext;
import com.zfoo.net.core.HostAndPort;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.SessionUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import fun.jiucai.common.constant.ChatAIEnum;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessageNotice;
import fun.jiucai.common.protocol.chatgpt.TransferChatgptNotify;
import fun.jiucai.home.chatai.xunfei.XunfeiWebsocketListener;
import fun.jiucai.home.chatai.xunfei.client.WebsocketClientXunFei;
import fun.jiucai.home.chatai.xunfei.client.XunFeiChatRequest;
import fun.jiucai.home.chatai.xunfei.event.XunFeiMessageEvent;
import fun.jiucai.home.config.MyConfiguration;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author godotg
 */
@Slf4j
@Component
public class XunfeiService {

    public static String hostUrl = "https://spark-api.xf-yun.com/v1.1/chat";
    public static String domain = "general";

    @Autowired
    private MyConfiguration myConfiguration;

    private AtomicInteger atomic = new AtomicInteger(0);

    public void process(Session session, long requestSid, long requestId, List<ChatgptMessage> messages) {
        // 请求
        var header = new XunFeiChatRequest.Header();
        var chat = new XunFeiChatRequest.Chat();
        var parameter = new XunFeiChatRequest.Parameter();
        var payload = new XunFeiChatRequest.Payload();
        var message = new XunFeiChatRequest.Message();

        var xunfeis = myConfiguration.getXunfei();
        var xunfeiConfiguration = xunfeis.get(Math.abs(atomic.incrementAndGet() % xunfeis.size()));

        //填充header
        header.setApp_id(xunfeiConfiguration.getAppId());
        header.setUid(RandomUtils.randomString(10));
        //填充parameter
        chat.setDomain(domain);
        chat.setTemperature(0.5);
        chat.setMax_tokens(4000);
        parameter.setChat(chat);

        var chatMessages = new ArrayList<XunFeiChatRequest.Text>();
        for (var chatgptMessage : messages) {
            chatMessages.add(new XunFeiChatRequest.Text(chatgptMessage.getRole(), chatgptMessage.getContent()));
        }

        //填充payload
        payload.setMessage(message);
        message.setText(chatMessages);
        var xunFeiChatRequest = new XunFeiChatRequest(header, parameter, payload);

        var xfSession = createClient(xunfeiConfiguration.getApiKey(), xunfeiConfiguration.getApiSecret());
        var attrData = xfSession.getChannel().attr(SESSION_MESSAGE_KEY);
        attrData.set(new XunfeiMessage(session, requestSid, requestId, messages));

        SchedulerBus.schedule(() -> xfSession.getChannel().writeAndFlush(xunFeiChatRequest), 2000, TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    public void processByOkHttp(Session session, long requestSid, long requestId, List<ChatgptMessage> messages) {
        var xunfeis = myConfiguration.getXunfei();
        var xunfeiConfiguration = xunfeis.get(Math.abs(atomic.incrementAndGet() % xunfeis.size()));
        var xunfei = new XunfeiWebsocketListener(session, requestId, requestSid, xunfeiConfiguration.getAppId(), xunfeiConfiguration.getApiKey(), xunfeiConfiguration.getApiSecret(), messages);
        xunfei.start();
    }

    @SneakyThrows
    public Session createClient(String apiKey, String apiSecret) {
        //构建鉴权httpurl
        var authUrl = getAuthorizationUrl(hostUrl, apiKey, apiSecret);
        var url = authUrl.replace("https://", "wss://").replace("http://", "ws://");

        var webSocketClientProtocolConfig = WebSocketClientProtocolConfig.newBuilder()
                .webSocketUri(url)
                .build();

        var client = new WebsocketClientXunFei(HostAndPort.valueOf("spark-api.xf-yun.com", 80), webSocketClientProtocolConfig);
        var session = client.start();
        return session;
    }

    //鉴权url
    @SneakyThrows
    public String getAuthorizationUrl(String hostUrl, String apikey, String apisecret) {
        //获取host
        var url = new URL(hostUrl);
        //获取鉴权时间 date
        var format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        var date = format.format(new Date());
        //获取signature_origin字段
        var builder = new StringBuilder("host: ").append(url.getHost()).append("\n").
                append("date: ").append(date).append("\n").
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        //获得signatue
        var mac = Mac.getInstance("hmacsha256");
        var sp = new SecretKeySpec(apisecret.getBytes(StringUtils.DEFAULT_CHARSET), "hmacsha256");
        mac.init(sp);
        var basebefore = mac.doFinal(builder.toString().getBytes(StringUtils.DEFAULT_CHARSET));
        var signature = Base64.getEncoder().encodeToString(basebefore);
        //获得 authorization_origin
        var authorization_origin = String.format("api_key=\"%s\",algorithm=\"%s\",headers=\"%s\",signature=\"%s\"", apikey, "hmac-sha256", "host date request-line", signature);
        //获得authorization
        var authorization = Base64.getEncoder().encodeToString(authorization_origin.getBytes(StringUtils.DEFAULT_CHARSET));
        //获取httpurl
        var httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().//
                addQueryParameter("authorization", authorization).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        return httpUrl.toString();
    }


    // -----------------------------------------------------------------------------------------------------------
    @Data
    @AllArgsConstructor
    public static class XunfeiMessage {
        private Session session;
        private long requestSid;
        private long requestId;
        private List<ChatgptMessage> messages;
    }

    public static final AttributeKey<XunfeiMessage> SESSION_MESSAGE_KEY = AttributeKey.valueOf("xunfei-message");


    @EventReceiver
    public void onXunFeiMessageEvent(XunFeiMessageEvent event) {
        var session = event.getSession();
        var response = event.getResponse();
        var requestSid = event.getRequestSid();
        var requestId = event.getRequestId();
        var chatRequestId = Integer.parseInt(StringUtils.format("{}{}", ChatAIEnum.xunfei.getType(), requestId));


        if (0 != response.getHeader().getCode()) {
            log.error("返回结果错误[{}]", JsonUtils.object2String(response));
            var notice = new ChatgptMessageNotice(chatRequestId, ChatAIEnum.xunfei.getType(), response.getHeader().getMessage(), ChatgptMessageNotice.STOP);
            var notify = new TransferChatgptNotify(requestSid, notice);
            NetContext.getRouter().send(session, notify);
            return;
        }

        var payload = response.getPayload();
        if (payload == null) {
            log.error("payload is null response:[{}]", JsonUtils.object2String(response));
            return;
        }

        var choices = payload.getChoices().getText();

        if (CollectionUtils.isEmpty(choices)) {
            log.error("返回结果为空[{}]", JsonUtils.object2String(response));
            return;
        }
        var choice = choices.get(0);
        var content = choice.getContent();

        if (!SessionUtils.isActive(session)) {
            throw new RuntimeException("session is inactive and stop chatgpt");
        }

        var finishReason = ChatgptMessageNotice.GENERATING;

        var status = response.getHeader().getStatus();
        if (status == 2) {
            finishReason = ChatgptMessageNotice.STOP;
        } else if (status == 0 || status == 1) {

        }

        var notice = new ChatgptMessageNotice(chatRequestId, ChatAIEnum.xunfei.getType(), content, finishReason);
        var notify = new TransferChatgptNotify(requestSid, notice);
        NetContext.getRouter().send(session, notify);
    }

}
