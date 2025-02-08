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

package fun.jiucai.home.midjourney;

import com.neovisionaries.ws.client.WebSocketFactory;
import com.zfoo.event.manager.EventBus;
import com.zfoo.event.model.AppStartEvent;
import fun.jiucai.common.util.HttpProxyUtils;
import fun.jiucai.home.config.MyConfiguration;
import fun.jiucai.home.midjourney.model.DiscordMessageReceivedEvent;
import fun.jiucai.home.midjourney.model.DiscordMessageUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 */
@Slf4j
@Component
public class DiscordService extends ListenerAdapter implements ApplicationListener<AppStartEvent>, Ordered {

    @Autowired
    private MyConfiguration myConfiguration;

    private JDA jda;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        this.jda = JDABuilder.createDefault(myConfiguration.getMidjourney().getBotToken()).setHttpClient(createOkHttpClient())
                .setWebsocketFactory(createWebSocketFactory())
                .addEventListeners(this)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build();
        log.info("discord bot start success");
    }

    public MessageChannel channel() {
        var channel = jda.getChannelById(MessageChannel.class, myConfiguration.getMidjourney().getChannelId());
        return channel;
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        EventBus.post(new DiscordMessageReceivedEvent(event));
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        EventBus.post(new DiscordMessageUpdateEvent(event));
    }


    private WebSocketFactory createWebSocketFactory() {
        var webSocketFactory = new WebSocketFactory().setConnectionTimeout(10000);
        var proxySettings = webSocketFactory.getProxySettings();
        proxySettings.setHost("127.0.0.1");
        proxySettings.setPort(10809);
        return webSocketFactory;
    }

    private OkHttpClient createOkHttpClient() {
        var client = new OkHttpClient.Builder().proxy(HttpProxyUtils.PROXY_SOCKET).build();
        return client;
    }

}
