package fun.jiucai.cloud.service;

import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.core.HostAndPort;
import com.zfoo.net.core.websocket.WebsocketServer;
import com.zfoo.net.core.websocket.WebsocketSslServer;
import com.zfoo.net.util.NetUtils;
import com.zfoo.protocol.util.ClassUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ServerService implements ApplicationListener<AppStartEvent> {

    @Value("${jiucai.cloud.host}")
    private String host;
    @Value("${jiucai.cloud.port}")
    private int port;
    @Value("${myspring.profiles.active}")
    private String profile;

    /**
     * 阿里云证书转换：
     * 进入阿里云证书下载页面，如下图。要下载两份，一个是tomcat的，一个是nginx的。
     * 下载的nginx格式的证书，里面包含pem和key两种格式的文件。其中pem格式我们可以直接用，key格式的Java无法直接使用。
     * 我们使用OpenSSL将前面下载tomcat格式证书pfx文件转换一个可以使用的key文件出来:
     * openssl pkcs12 -in jiucai.fun.pfx -nocerts -nodes -out ws.jiucai.fun.key
     * <p>
     * openssl会让输入密码，密码是tomcat下载文件里的pfx-password.txt
     */
    @SneakyThrows
    @Override
    public void onApplicationEvent(AppStartEvent event) {
        if ("dev".equals(profile)) {
            var websocketServer = new WebsocketServer(HostAndPort.valueOf(host, port));
            websocketServer.start();
        } else {
            var pem = ClassUtils.getFileFromClassPath("ws.jiucai.fun.pem");
            var key = ClassUtils.getFileFromClassPath("ws.jiucai.fun.key");
            var websocketSslServer = new WebsocketSslServer(HostAndPort.valueOf(NetUtils.getLocalhostStr(), port), pem, key);
            websocketSslServer.start();
        }
    }

}
