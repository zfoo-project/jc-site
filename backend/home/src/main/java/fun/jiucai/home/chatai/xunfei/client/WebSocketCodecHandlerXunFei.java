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

import com.zfoo.protocol.buffer.ByteBufUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * header(4byte) + protocolId(2byte) + packet
 * header = body(bytes.length) + protocolId.length(2byte)
 *
 * @author godotg
 */
public class WebSocketCodecHandlerXunFei extends MessageToMessageCodec<WebSocketFrame, XunFeiChatRequest> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame, List<Object> list) {
        ByteBuf in = webSocketFrame.content();

        var bytes = ByteBufUtils.readAllBytes(in);
        var json = StringUtils.bytesToString(bytes);
        var response = JsonUtils.string2Object(json, XunFeiChatResponse.class);
        list.add(response);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, XunFeiChatRequest out, List<Object> list) {
        var json = JsonUtils.object2String(out);
        list.add(new TextWebSocketFrame(json));
    }

}
