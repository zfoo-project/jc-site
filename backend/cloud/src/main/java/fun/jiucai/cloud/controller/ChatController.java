package fun.jiucai.cloud.controller;

import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.SessionUtils;
import com.zfoo.scheduler.util.SingleCache;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.model.CodeEnum;
import fun.jiucai.cloud.model.chat.MessageEnum;
import fun.jiucai.common.entity.MessageEntity;
import fun.jiucai.common.protocol.chat.*;
import fun.jiucai.cloud.util.xdb.XdbUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ChatController implements ApplicationListener<AppStartEvent> {

    public static final int CHAT_MESSAGE_QUERY_LIMIT = 64;

    public static SingleCache<Long> maxMessageIdCache;

    @EntityCacheAutowired
    private IEntityCache<Long, MessageEntity> messageEntityCaches;

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        maxMessageIdCache = SingleCache.build(10 * TimeUtils.MILLIS_PER_SECOND, () -> MongoIdUtils.getMaxIdFromMongoDefault(MessageEntity.class));
    }

    @PacketReceiver
    public void atGroupChatRequest(Session session, GroupChatRequest request) {
        var groupId = request.getGroupId();
        var type = MessageEnum.getMessageEnumByType(request.getType());
        var message = request.getMessage();

        log.info("group chat type:[{}] message:[{}]", type, message);
        if (type == null) {
            NetContext.getRouter().send(session, Error.valueOf(CodeEnum.PARAMETER_ERROR.getMessage()));
            return;
        }

        if (StringUtils.isBlank(message)) {
            NetContext.getRouter().send(session, Error.valueOf(CodeEnum.CHAT_MESSAGE_IS_EMPTY.getMessage()));
            return;
        }

        var id = MongoIdUtils.getIncrementIdFromMongoDefault(MessageEntity.class);
        var sendId = SessionUtils.toIpLong(session);
        var region = XdbUtils.search(SessionUtils.toIp(session));

        var entity = new MessageEntity(id, type.getType(), sendId, region, message, TimeUtils.now());
        OrmContext.getAccessor().insert(entity);
        maxMessageIdCache.set(id);

        var chatMessage = toChatMessage(entity);
        var groupMessageNotice = new GroupChatNotice(List.of(chatMessage));
        NetContext.getSessionManager().forEachServerSession(it -> NetContext.getRouter().send(it, groupMessageNotice));
    }

    @PacketReceiver
    public void atGroupHistoryMessageRequest(Session session, GroupHistoryMessageRequest request) {
        var groupId = request.getGroupId();
        var lastMessageId = request.getLastMessageId();

        if (lastMessageId <= 0) {
            lastMessageId = MongoIdUtils.getMaxIdFromMongoDefault(MessageEntity.class);
        } else {
            lastMessageId = Math.max(1, lastMessageId - 1);
        }

        var fromId = Math.max(1, lastMessageId - CHAT_MESSAGE_QUERY_LIMIT);
        var list = new ArrayList<ChatMessage>();
        for (var i = fromId; i <= lastMessageId; i++) {
            var entity = messageEntityCaches.load(i);
            if (entity == null) {
                continue;
            }
            list.add(toChatMessage(entity));
        }

        var onlineUsers = NetContext.getSessionManager().serverSessionSize();
        NetContext.getRouter().send(session, new GroupHistoryMessageResponse(groupId, list, onlineUsers));
    }

    public ChatMessage toChatMessage(MessageEntity entity) {
        return new ChatMessage(entity.getId(), entity.getType(), entity.getSendId(), entity.getRegion(), entity.getMessage(), entity.getTimestamp());
    }

}
