package fun.jiucai.cloud.broker;

import com.zfoo.event.anno.EventReceiver;
import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.anno.Task;
import com.zfoo.net.core.HostAndPort;
import com.zfoo.net.core.event.ServerSessionInactiveEvent;
import com.zfoo.net.core.tcp.TcpServer;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.NetUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.collection.concurrent.ConcurrentHashSet;
import com.zfoo.protocol.util.RandomUtils;
import fun.jiucai.common.protocol.broker.BrokerRegisterAnswer;
import fun.jiucai.common.protocol.broker.BrokerRegisterAsk;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author godotg
 */
@Slf4j
@Component
public class BrokerService implements ApplicationListener<AppStartEvent> {

    @Value("${jiucai.broker.host}")
    private String host;
    @Value("${jiucai.broker.port}")
    private int port;

    // key:brokerType -> value:sessions
    private ConcurrentHashMap<Integer, ConcurrentHashSet<Long>> brokerSessionMap = new ConcurrentHashMap<>();


    @SneakyThrows
    @Override
    public void onApplicationEvent(AppStartEvent event) {
        var localHost = NetUtils.getLocalhostStr();
        var brokerServer = new TcpServer(HostAndPort.valueOf(host, port));
        brokerServer.start();
    }


    @PacketReceiver(Task.NettyIO)
    public void atBrokerRegisterAsk(Session session, BrokerRegisterAsk ask) {
        var sid = session.getSid();
        var brokerType = ask.getBrokerType();
        var brokerSessions = brokerSessionMap.computeIfAbsent(brokerType, it -> new ConcurrentHashSet<>());
        brokerSessions.add(sid);
        NetContext.getRouter().send(session, new BrokerRegisterAnswer());
        log.info("broker server 注册成功 [sid:{}]", session.getSid());
    }

    @EventReceiver
    public void onServerSessionInactiveEvent(ServerSessionInactiveEvent event) {
        for (var brokerSessions : brokerSessionMap.values()) {
            brokerSessions.remove(event.getSession().getSid());
        }
    }

    public boolean existBroker(int brokerType) {
        var brokerSessions = brokerSessionMap.get(brokerType);
        return CollectionUtils.isNotEmpty(brokerSessions);
    }

    public Session brokerSession(int brokerType) {
        var brokerSessions = brokerSessionMap.get(brokerType);
        if (CollectionUtils.isEmpty(brokerSessions)) {
            throw new RuntimeException("broker sessions is empty");
        }
        var brokerSid = RandomUtils.randomEle(new ArrayList<>(brokerSessions));
        var brokerSession = NetContext.getSessionManager().getServerSession(brokerSid);
        if (brokerSession == null) {
            throw new RuntimeException("broker sessions is not exist");
        }
        return brokerSession;
    }

    public void broker(int brokerType, Object packet) {
        var brokerSession = brokerSession(brokerType);
        NetContext.getRouter().send(brokerSession, packet);
    }

}
