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

package fun.jiucai.cloud.wechat;

import com.zfoo.event.anno.EventReceiver;
import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.anno.Task;
import com.zfoo.net.core.HostAndPort;
import com.zfoo.net.core.websocket.WebsocketServer;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.SessionUtils;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.util.SingleCache;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.model.CodeEnum;
import fun.jiucai.cloud.util.TokenUtils;
import fun.jiucai.cloud.wechat.event.WeChatMessageEvent;
import fun.jiucai.cloud.wechat.model.AccessTokenResponse;
import fun.jiucai.cloud.wechat.model.QrCodeRequest;
import fun.jiucai.cloud.wechat.model.QrCodeResponse;
import fun.jiucai.common.entity.AccountWeChatEntity;
import fun.jiucai.common.entity.UserEntity;
import fun.jiucai.common.protocol.auth.LoginByWeChatRequest;
import fun.jiucai.common.protocol.auth.LoginByWeChatResponse;
import fun.jiucai.common.protocol.user.UserProfileNotice;
import fun.jiucai.common.util.HttpUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author godotg
 */
@Slf4j
@Controller
@CrossOrigin
public class WechatLoginController {

    /**
     * 获取用户认证授权URL
     */
    public static final String QR_CODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token={}";


    // 获取用户access_token和openid信息URL，微信只有从微信客户端用网页授权的登录方式才能获取到用户的信息，所以如果想要双端登录就无法获取用户信息，普通浏览器无法打开网页授权的登录方式
    @Value("${myspring.profiles.active}")
    private String profile;
    @EntityCacheAutowired
    private IEntityCache<Long, UserEntity> userEntityCaches;
    @Autowired
    private WeChatService weChatService;

    @PacketReceiver(Task.EventBus)
    public void atLoginByWeChatRequest(Session session, LoginByWeChatRequest request) throws IOException, InterruptedException {
        var sid = session.getSid();
        if ("dev".equals(profile)) {
            devLogin(session);
            return;
        }

        var qrRequest = new QrCodeRequest();
        var actionInfo = new QrCodeRequest.ActionInfo();
        qrRequest.setAction_name("QR_STR_SCENE");
        qrRequest.setExpire_seconds(5 * 60);
        qrRequest.setAction_info(actionInfo);
        var scene = new QrCodeRequest.Scene();
        // 设置场景值
        scene.setScene_str(StringUtils.format("{}_{}", RandomUtils.randomString(16), sid));
        actionInfo.setScene(scene);
        var accessToken = weChatService.accessTokenCache.get();
        var url = StringUtils.format(QR_CODE_URL, accessToken);
        var qrJson = HttpUtils.post(url, qrRequest);
        var qrResponse = JsonUtils.string2Object(qrJson, QrCodeResponse.class);

        // {"ticket":"gQEJ8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAycmFNNmRPUkRjVjQxNWV5MjFEYy0AAgQiYQJnAwQsAQAA","expire_seconds":300,"url":"http:\/\/weixin.qq.com\/q\/02raM6dORDcV415ey21Dc-"}
        log.info("accessToken:[{}] url:[{}] qr:[{}]", accessToken, url, qrJson);
        if (StringUtils.isEmpty(qrResponse.getUrl())) {
            NetContext.getRouter().send(session, Error.valueOf(CodeEnum.SIGN_IN_WECHAT_QR_CODE_ERROR.getMessage()));
            return;
        }
        NetContext.getRouter().send(session, new LoginByWeChatResponse(qrResponse.getUrl()));
    }

    @EventReceiver
    public void onWeChatMessageEvent(WeChatMessageEvent messageEvent) {
        var message = messageEvent.getMessage();
        var event = message.getEvent();
        var eventKey = message.getEventKey();


        switch (event.toLowerCase()) {
            // 用户未关注时，进行关注后的事件推送
            case "subscribe":
                break;
            // 用户已关注时的事件推送
            // {"event":"SCAN","eventKey":"9TCSMPxWWaOYl1Nf_87","msgType":"event","toUserName":"gh_20cde1ddb5b2","fromUserName":"o9Sc5xEtAB0Mcjgd3O-Dfg1tCRJc","createTime":1728204749,"ticket":"gQEX8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyTWpkMWRhUkRjVjQxN09nMmhEY3QAAgTGTwJnAwQsAQAA"}
            case "scan":
                var openId = message.getFromUserName();
                EventBus.asyncExecute(openId.hashCode(), () -> doLogin(eventKey, openId));
                break;

            default:
        }
    }

    // 模拟开发环境的登录
    public void devLogin(Session session) {
        var authUrl = "http:\\/\\/weixin.qq.com\\/q\\/02raM6dORDcV415ey21Dc-";
        NetContext.getRouter().send(session, new LoginByWeChatResponse(authUrl));

        SchedulerBus.schedule(() -> {
            var sid = session.getSid();
            var eventKey = StringUtils.format("{}_{}", RandomUtils.randomString(16), sid);
            var weChatId = SessionUtils.toIp(session);
            doLogin(eventKey, weChatId);
        }, 5, TimeUnit.SECONDS);
    }

    public void doLogin(String eventKey, String weChatId) {
        var sid = Long.parseLong(StringUtils.substringAfterLast(eventKey, "_"));
        var session = NetContext.getSessionManager().getServerSession(sid);
        if (StringUtils.isEmpty(weChatId)) {
            NetContext.getRouter().send(session, Error.valueOf(CodeEnum.SIGN_IN_FAIL.getMessage()));
            return;
        }

        var account = OrmContext.getAccessor().load(weChatId, AccountWeChatEntity.class);
        if (account == null) {
            account = createUserByWeChatId(weChatId);
        }

        var userId = account.getUid();
        var userEntity = userEntityCaches.load(userId);
        session.setUid(userId);
        var token = TokenUtils.encrypt(userId);
        NetContext.getRouter().send(session, new UserProfileNotice(userEntity.toUser(), token));
    }

    public AccountWeChatEntity createUserByWeChatId(String weChatId) {
        var uid = MongoIdUtils.getIncrementIdFromMongoDefault(UserEntity.class);
        var newAccount = new AccountWeChatEntity();
        newAccount.setId(weChatId);
        newAccount.setUid(uid);
        OrmContext.getAccessor().insert(newAccount);

        var newUser = new UserEntity();
        newUser.setId(uid);
        newUser.setName(weChatId);
        newUser.setCtime(TimeUtils.now());
        OrmContext.getAccessor().insert(newUser);

        return newAccount;
    }

}