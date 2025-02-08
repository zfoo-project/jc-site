package fun.jiucai.cloud.controller;

import com.zfoo.event.anno.EventReceiver;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.SessionUtils;
import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.model.CodeEnum;
import fun.jiucai.cloud.model.event.ChatgptEvent;
import fun.jiucai.cloud.util.TokenUtils;
import fun.jiucai.cloud.util.xdb.XdbUtils;
import fun.jiucai.common.entity.UserEntity;
import fun.jiucai.cloud.statistic.event.StatMidImagineRequestEvent;
import fun.jiucai.cloud.statistic.event.StatSdSimulateRequestEvent;
import fun.jiucai.common.protocol.auth.LoginRequest;
import fun.jiucai.common.protocol.auth.LoginResponse;
import fun.jiucai.common.protocol.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author godotg
 */
@Slf4j
@Component
public class UserController {

    @EntityCacheAutowired
    private IEntityCache<Long, UserEntity> userEntityCaches;

    @PacketReceiver
    public void atLoginRequest(Session session, LoginRequest request) {
        var ip = SessionUtils.toIp(session);
        var ipLong = SessionUtils.toIpLong(session);
        var sid = session.getSid();
        var activeUid = MongoIdUtils.getIncrementIdFromMongoDefault("activeUid");
        var region = XdbUtils.search(ip);
        var token = request.getToken();

        log.info("login ip:[{}] sid:[{}] token:[{}]", ip, sid, token);

        var chatMessageIdDiff = ChatController.maxMessageIdCache.get() - request.getChatMessageId();
        if (chatMessageIdDiff > 99) {
            chatMessageIdDiff = 99;
        }

        // 微信登录
        var user = doLogin(session, token);

        var response = new LoginResponse(ip, ipLong, sid, activeUid, region, chatMessageIdDiff, user);
        NetContext.getRouter().send(session, response);
    }

    public User doLogin(Session session, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        TokenUtils.verify(token);
        var triple = TokenUtils.decrypt(token);
        var userId = triple.getLeft();
        var userEntity = userEntityCaches.load(userId);
        if (userEntity == null) {
            return null;
        }

        session.setUid(userId);
        userEntity.setLogin(userEntity.getLogin() + 1);
        userEntity.setLastLoginTime(TimeUtils.now());
        userEntityCaches.updateUnsafe(userEntity);
        return userEntity.toUser();
    }

    public UserEntity loadUser(Session session) {
        var uid = session.getUid();
        if (uid <= 0) {
            return null;
        }
        var userEntity = userEntityCaches.load(uid);
        return userEntity;
    }

    @PacketReceiver
    public void atUpdateUserProfileRequest(Session session, UpdateUserProfileRequest request) {
        var userEntity = loadUser(session);
        if (userEntity == null) {
            NetContext.getRouter().send(session, Error.valueOf(CodeEnum.SIGN_IN_FIRST.getMessage()));
            return;
        }

        var phoneNumber = request.getPhoneNumber();

        String regex = "^1[3456789]\\d{9}$"; // 中国手机号码的正则表达式
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(String.valueOf(phoneNumber));
        if (!matcher.matches()) {
            NetContext.getRouter().send(session, Error.valueOf(CodeEnum.SIGN_IN_PHONE_NUMBER_ERROR.getMessage()));
            return;
        }

        userEntity.setPhoneNumber(phoneNumber);
        userEntityCaches.update(userEntity);
        NetContext.getRouter().send(session, new UpdateUserProfileResponse(userEntity.toUser()));
    }

    @PacketReceiver
    public void atGetUserProfileRequest(Session session, GetUserProfileRequest request) {
        var userEntity = loadUser(session);
        if (userEntity == null) {
            NetContext.getRouter().send(session, Error.valueOf(CodeEnum.SIGN_IN_FIRST.getMessage()));
            return;
        }
        NetContext.getRouter().send(session, new GetUserProfileResponse(userEntity.toUser()));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @EventReceiver
    public void onChatgptEvent(ChatgptEvent event) {
        var userEntity = loadUser(event.getSession());
        if (userEntity == null) {
            return;
        }
        userEntity.setAsk(userEntity.getAsk() + 1);
        userEntity.setCost(userEntity.getCost() + 1);
        userEntityCaches.update(userEntity);
    }

    @EventReceiver
    public void onStatMidImagineRequestEvent(StatMidImagineRequestEvent event) {
        var userEntity = loadUser(event.getSession());
        if (userEntity == null) {
            return;
        }
        userEntity.setDraw(userEntity.getDraw() + 1);
        userEntity.setCost(userEntity.getCost() + 10);
        userEntityCaches.update(userEntity);
    }

    @EventReceiver
    public void onStatSdSimulateRequestEvent(StatSdSimulateRequestEvent event) {
        var userEntity = loadUser(event.getSession());
        if (userEntity == null) {
            return;
        }
        userEntity.setDraw(userEntity.getDraw() + 1);
        userEntity.setCost(userEntity.getCost() + 5);
        userEntityCaches.update(userEntity);
    }

}
