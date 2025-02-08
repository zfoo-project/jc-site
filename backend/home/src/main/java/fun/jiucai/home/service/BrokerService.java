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

package fun.jiucai.home.service;

import com.zfoo.event.anno.EventReceiver;
import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.NetContext;
import com.zfoo.net.core.HostAndPort;
import com.zfoo.net.core.event.ClientSessionInactiveEvent;
import com.zfoo.net.core.tcp.TcpClient;
import com.zfoo.net.util.NetUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.collection.concurrent.ConcurrentHashSet;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.scheduler.anno.Scheduler;
import fun.jiucai.common.protocol.broker.BrokerRegisterAnswer;
import fun.jiucai.common.protocol.broker.BrokerRegisterAsk;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author godotg
 */
@Slf4j
@Component
public class BrokerService implements ApplicationListener<AppStartEvent> {

    private static final int BROKER_NUM = 2;

    @Value("${jiucai.broker.host}")
    private String brokerHost;
    @Value("${jiucai.broker.port}")
    private int brokerPort;
    @Value("${myspring.profiles.active}")
    private String profile;

    private ConcurrentHashSet<Long> brokerSessions = new ConcurrentHashSet<>();

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        if ("dev".equals(profile)) {
            brokerHost = NetUtils.getLocalhostStr();
        }
    }

    // 保持一定数量的broker
    @Scheduler(cron = "0/1 * * * * ?")
    public void cronBroker() {
        if (brokerSessions.size() < BROKER_NUM) {
            createBroker();
        }
    }

    @SneakyThrows
    public void createBroker() {
        var tcpClient = new TcpClient(HostAndPort.valueOf(brokerHost, brokerPort));
        var session = tcpClient.start();
        var ask = new BrokerRegisterAsk(BrokerRegisterAsk.HOME);
        var answer = NetContext.getRouter().syncAsk(session, ask, BrokerRegisterAnswer.class, null).packet();
        brokerSessions.add(session.getSid());
        log.info("broker client 注册成功 [sid:{}]", session.getSid());
    }

    @EventReceiver
    public void onClientSessionInactiveEvent(ClientSessionInactiveEvent event) {
        brokerSessions.remove(event.getSession().getSid());
    }


    public void broker(Object packet) {
        if (CollectionUtils.isEmpty(brokerSessions)) {
            throw new RuntimeException("broker sessions is empty");
        }
        var brokerSid = RandomUtils.randomEle(new ArrayList<>(brokerSessions));
        var brokerSession = NetContext.getSessionManager().getClientSession(brokerSid);
        if (brokerSession == null) {
            throw new RuntimeException("broker sessions is not exist");
        }
        NetContext.getRouter().send(brokerSession, packet);
    }

}
