package fun.jiucai.cloud.service;

import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.util.SessionUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.LazyCache;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.anno.StorageAutowired;
import com.zfoo.storage.model.IStorage;
import fun.jiucai.cloud.model.chat.MessageData;
import fun.jiucai.cloud.resource.ChatNoticeResource;
import fun.jiucai.cloud.service.mychatgpt.MyChatgptService;
import fun.jiucai.cloud.service.mychatgpt.MyOpenAi;
import fun.jiucai.common.constant.ChatAIEnum;
import fun.jiucai.common.protocol.chatgpt.ChatgptForceStopResponse;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessageNotice;
import io.reactivex.functions.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ChatgptService {
    @StorageAutowired
    private IStorage<Integer, ChatNoticeResource> chatNoticeStorage;

    @Autowired
    private MyChatgptService myChatgptService;

    // -----------------------------------------------------------------------------------------------------------------
    // key为 sid + requestId
    public LazyCache<String, MessageData> processCache = new LazyCache<>(1000, 3 * TimeUtils.MILLIS_PER_MINUTE, 1 * TimeUtils.MILLIS_PER_MINUTE, null);


    public String requestUUID(long sid, long requestId) {
        return StringUtils.format("{}-{}", sid, requestId);
    }

    public String randomChatNotice(int type) {
        var chatNotice = RandomUtils.randomEle(new ArrayList<>(chatNoticeStorage.getIndexes(ChatNoticeResource::getType, type)));
        return chatNotice.getWord() + "\uD83D\uDE0E";
    }

    public void process(MessageData data) {
        var sid = data.getSid();
        var request = data.getRequest();
        var session = NetContext.getSessionManager().getServerSession(sid);

        var messages = request.getMessages();
        var chatMessages = new ArrayList<ChatMessage>();
        for (var message : messages) {
            chatMessages.add(new ChatMessage(message.getRole(), message.getContent()));
        }

        var chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(chatMessages)
                .n(1)
                .stream(true)
                .build();

        var requestUUID = requestUUID(sid, request.getRequestId());
        processCache.put(requestUUID, data);

        var callback = new Consumer<ChatCompletionChunk>() {
            @Override
            public void accept(ChatCompletionChunk chatCompletionChunk) {
                if (data.isForceStop()) {
                    processCache.remove(requestUUID);
                    NetContext.getRouter().send(session, new ChatgptMessageNotice(request.getRequestId(), ChatAIEnum.chatgpt.getType(), "\uD83D\uDE05", ChatgptMessageNotice.STOP));
                    throw new RuntimeException("force stop chatgpt");
                }

                var choices = chatCompletionChunk.getChoices();
                if (CollectionUtils.isEmpty(choices)) {
                    return;
                }
                var choice = choices.get(0);
                var chatMessage = choice.getMessage();
                var content = StringUtils.EMPTY;
                if (chatMessage != null) {
                    content = chatMessage.getContent();
                }

                if (!SessionUtils.isActive(session)) {
                    throw new RuntimeException("session is inactive and stop chatgpt");
                }
                var finishReason = ChatgptMessageNotice.GENERATING;
                if ("stop".equals(choice.getFinishReason())) {
                    finishReason = ChatgptMessageNotice.STOP;
                    processCache.remove(requestUUID);
                } else if (StringUtils.isNotBlank(choice.getFinishReason())) {
                    finishReason = ChatgptMessageNotice.EXCEPTION;
                    processCache.remove(requestUUID);
                    content = content + "\uD83D\uDC79";
                }
                NetContext.getRouter().send(session, new ChatgptMessageNotice(request.getRequestId(), ChatAIEnum.chatgpt.getType(), content, finishReason));
            }
        };

        if (myChatgptService.isEmpty()) {
            NetContext.getRouter().send(session, new ChatgptForceStopResponse());
            NetContext.getRouter().send(session, Message.valueSuccess("openai没有配置"));
            return;
        }

        MyOpenAi myOpenAi = myChatgptService.myOpenAi();
        log.info("myOpenAi url:[{}] token:[{}]", myOpenAi.url(), myOpenAi.token());
        try {
//            if (myChatgptService.isEmpty()) {
//                openAiManager.service().streamChatCompletion(chatCompletionRequest).blockingForEach(callback);
//                return;
//            }

            myOpenAi.streamChatCompletion(chatCompletionRequest).blockingForEach(callback);
        } catch (Exception e) {
            var exception = StringUtils.trim(e.getMessage());
            if (exception.contains("余额")) {
                myChatgptService.close(myOpenAi);
            }

            if (data.isForceStop()) {
                return;
            }
            // 如果使用了自己的my chatgpt失败了，则检测一下余额
            myChatgptService.checkUsage();

            var contentTotalSize = messages.stream().mapToInt(it -> it.getContent().length()).count();
            log.error("talkerror [sid:{}] [length:{}] [size:{}] [{}]", sid, messages.size(), contentTotalSize, messages.get(messages.size() - 1));
            NetContext.getRouter().send(session, new ChatgptMessageNotice(request.getRequestId(), ChatAIEnum.chatgpt.getType(), randomChatNotice(ChatNoticeResource.SEVER_TIMEOUT), ChatgptMessageNotice.EXCEPTION));
            processCache.remove(requestUUID);
            throw e;
        }
    }
}
