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

package fun.jiucai.home.chatai.xunfei.client;

import com.zfoo.event.manager.EventBus;
import com.zfoo.net.handler.ClientRouteHandler;
import com.zfoo.net.util.SessionUtils;
import fun.jiucai.home.chatai.xunfei.event.XunFeiClientSessionActiveEvent;
import fun.jiucai.home.chatai.xunfei.event.XunFeiClientSessionInactiveEvent;
import fun.jiucai.home.chatai.xunfei.event.XunFeiMessageEvent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fun.jiucai.home.chatai.XunfeiService.SESSION_MESSAGE_KEY;

/**
 * @author godotg
 */
@ChannelHandler.Sharable
public class ClientRouteHandlerXunFei extends ClientRouteHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientRouteHandlerXunFei.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        var session = SessionUtils.getSession(ctx);
        EventBus.post(XunFeiClientSessionActiveEvent.valueOf(session));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        var session = SessionUtils.getSession(ctx);
        EventBus.post(XunFeiClientSessionInactiveEvent.valueOf(session));
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        var session = SessionUtils.getSession(ctx);
        if (session == null) {
            return;
        }
        var response = (XunFeiChatResponse) msg;

        var attrData = session.getChannel().attr(SESSION_MESSAGE_KEY);
        var data = attrData.get();

        var requestSid = data.getRequestSid();
        var requestId = data.getRequestId();

        EventBus.post(new XunFeiMessageEvent(data.getSession(), response, requestId, requestSid));
    }

}
