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

import com.zfoo.net.NetContext;
import com.zfoo.net.core.AbstractClient;
import com.zfoo.net.core.HostAndPort;
import com.zfoo.net.handler.BaseRouteHandler;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.exception.ExceptionUtils;
import com.zfoo.protocol.util.IOUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @author godotg
 */
public class WebsocketClientXunFei extends AbstractClient<SocketChannel> {

    private final WebSocketClientProtocolConfig webSocketClientProtocolConfig;

    public WebsocketClientXunFei(HostAndPort host, WebSocketClientProtocolConfig webSocketClientProtocolConfig) {
        super(host);
        this.webSocketClientProtocolConfig = webSocketClientProtocolConfig;
    }

    @Override
    public synchronized Session start() {
        return doStart();
    }

    private synchronized Session doStart() {
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(nioEventLoopGroup)
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(16 * IOUtils.BYTES_PER_KB, 16 * IOUtils.BYTES_PER_MB))
                .handler(this);
        var channelFuture = bootstrap.connect(hostAddress, port);
        channelFuture.syncUninterruptibly();

        if (channelFuture.isSuccess()) {
            if (channelFuture.channel().isActive()) {
                var channel = channelFuture.channel();
                var session = BaseRouteHandler.initChannel(channel);
                NetContext.getSessionManager().addClientSession(session);
                return session;
            }
        } else if (channelFuture.cause() != null) {
            logger.error(ExceptionUtils.getMessage(channelFuture.cause()));
        } else {
            logger.error("[{}] started failed", this.getClass().getSimpleName());
        }
        return null;
    }

    @Override
    public void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new HttpClientCodec(8 * IOUtils.BYTES_PER_KB, 16 * IOUtils.BYTES_PER_KB, 16 * IOUtils.BYTES_PER_KB));
        channel.pipeline().addLast(new HttpObjectAggregator(16 * IOUtils.BYTES_PER_MB));
        channel.pipeline().addLast(new WebSocketClientProtocolHandler(webSocketClientProtocolConfig));
        channel.pipeline().addLast(new ChunkedWriteHandler());
        channel.pipeline().addLast(new WebSocketCodecHandlerXunFei());
        channel.pipeline().addLast(new ClientRouteHandlerXunFei());
    }
}
