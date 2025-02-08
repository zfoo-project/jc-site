package fun.jiucai.home.midjourney;

import com.aliyun.oss.model.ObjectMetadata;
import com.zfoo.event.anno.Bus;
import com.zfoo.event.anno.EventReceiver;
import com.zfoo.net.util.security.AesUtils;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.NumberUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.entity.midjourney.MidjourneyEntity;
import fun.jiucai.home.manager.OssManager;
import fun.jiucai.common.protocol.midjourney.TransferMidImagineNotify;
import fun.jiucai.common.protocol.midjourney.MidImagineNotice;
import fun.jiucai.common.util.HttpProxyUtils;
import fun.jiucai.home.midjourney.model.DiscordMessageReceivedEvent;
import fun.jiucai.home.midjourney.model.DiscordMessageUpdateEvent;
import fun.jiucai.home.midjourney.model.MidjourneyTaskType;
import fun.jiucai.home.service.BrokerService;
import fun.jiucai.home.service.ImageService;
import fun.jiucai.home.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sun
 */
@Slf4j
@Component
public class MidjourneyManager {

    public static final String DISCORD_CDN_URL = "https://cdn.discordapp.com";


    @Autowired
    private MidjourneyService midjourneyService;
    @Autowired
    private BrokerService brokerService;
    @Autowired
    private OssManager ossManager;
    @Autowired
    private ImageService imageService;

    @EventReceiver(Bus.AsyncThread)
    public void onDiscordMessageReceivedEvent(DiscordMessageReceivedEvent discordMessageReceivedEvent) {
        var event = discordMessageReceivedEvent.getMessageReceivedEvent();
        var message = event.getMessage();
        String authorName = message.getAuthor().getName();
        log.info("--------------------------------------------------------------------------------------------------------------------------------------------------------");
        log.info("received type:[{}] - id:[{}] - nonce:[{}] - author:[{}] - content:[{}] - image:[{}]", message.getType(), message.getId(), message.getNonce(), authorName, message.getContentRaw(), getImageUrl(message));
        // 如果是机器人发送的消息则忽略，因为只需要监听Midjourney-Bot的消息
        if ("mj-listener".equals(authorName)) {
            return;
        }
        try {
            processMessage(message);
        } catch (Exception e) {
            log.error("处理消息未知异常", e);
        } catch (Throwable t) {
            log.error("处理消息错误", t);
        }
    }

    @EventReceiver(Bus.AsyncThread)
    public void onDiscordMessageUpdateEvent(DiscordMessageUpdateEvent discordMessageUpdateEvent) {
        var event = discordMessageUpdateEvent.getMessageUpdateEvent();
        var message = event.getMessage();
        String authorName = message.getAuthor().getName();
        log.info("update type:[{}] - id:[{}] - nonce:[{}] - author:[{}] - content:[{}] - image:[{}]", message.getType(), message.getId(), message.getNonce(), authorName, message.getContentRaw(), getImageUrl(message));


        var messageId = message.getIdLong();
        var content = simpleContent(message);

        var taskOptional = midjourneyService.consumers.stream().filter(it -> it.getDiscordMessageId() == messageId).findFirst();
        if (taskOptional.isEmpty()) {
            return;
        }
        var task = taskOptional.get();
        task.setDiscordMessageId(messageId);
        var notice = MidImagineNotice.valueOf(MidImagineNotice.update, task.getNonce(), content);
        notice.setProgress(progress(message));
        brokerService.broker(new TransferMidImagineNotify(task.getRequestSid(), notice));
    }

    public void processMessage(Message message) {
        var nonce = message.getNonce();
        var discordMessageId = message.getIdLong();
        var content = simpleContent(message);
        // received type:[DEFAULT] - id:[1178671457086226504] - nonce:[null] - author:[Midjourney Bot] - content:[**Chinese style zombies** - <@605602758384680972> (fast)] - image:[https://cdn.discordapp.com/attachments/1117348636100136981/1178671456784240752/project_zfoo_Chinese_style_zombies_6a0d7c3f-f928-49f0-b41c-de97a5543c90.png?ex=6576fe55&is=65648955&hm=e30747c686c044c54ed1b7c65af48d4a39e93d7784ab17b9af79c24b3a2bd67c&]
        // received type:[DEFAULT] - id:[1179321473329008722] - nonce:[null] - author:[Midjourney Bot] - content:[**Scholar and Grand Tutor** - <@605602758384680972> (relaxed)] - image:[https://cdn.discordapp.com/attachments/1117348636100136981/1179321472787947552/project_zfoo_Scholar_and_Grand_Tutor_fd9415b9-e276-4255-a493-dd462d457ea1.png?ex=65795bb5&is=6566e6b5&hm=a33e6019797027c3e2edd893a5d9ad12821fc92666b6719b53333cf7192845fa&]
        // received type:[DEFAULT] - id:[1179327613605519371] - nonce:[null] - author:[Midjourney Bot] - content:[**soybean oil** - <@605602758384680972> (relaxed)] - image:[https://cdn.discordapp.com/attachments/1117348636100136981/1179327613123178586/project_zfoo_soybean_oil_c654944e-a5ec-4c8e-9e7a-793bd6ffb633.png?ex=6579616d&is=6566ec6d&hm=51b23590f3f8d0aa81e66404cc49395199d3adfcc4e313201e23363a2992878d&]
        // received type:[DEFAULT] - id:[1179330811275780188] - nonce:[null] - author:[Midjourney Bot] - content:[**Coca Cola** - <@605602758384680972> (relaxed)] - image:[https://cdn.discordapp.com/attachments/1117348636100136981/1179330810738917435/project_zfoo_Coca_Cola_9eb3da52-c26d-4f45-8bbf-75eb3b34bc17.png?ex=65796468&is=6566ef68&hm=d3a77c30fa694c8f9d0b903441580e6d091310a4e91e646a60f940c86fc525f7&]
        // midjourney image name [9eb3da52-c26d-4f45-8bbf-75eb3b34bc17]
        if (StringUtils.isEmpty(nonce) || nonce.equals("null")) {
            midjourneyService.consumers.stream().forEach(it -> log.info("task -> [{}]", JsonUtils.object2String(it)));
            var discordImageUrl = getImageUrl(message);
            var imageName = StringUtils.substringAfterLast(discordImageUrl, StringUtils.SLASH);
            imageName = StringUtils.substringBeforeFirst(imageName, StringUtils.PERIOD);
            var imageRealName = StringUtils.substringAfterLast(imageName, "_");
            imageRealName = StringUtils.substringBeforeLast(imageRealName, ".");
            log.info("midjourney discordImageUrl:[{}] imageName:[{}] imageRealName:[{}]", discordImageUrl, imageName, imageRealName);


            var taskOptional = midjourneyService.consumers.stream().filter(it -> content.contains(it.getPromptWith())).findFirst();

            if (taskOptional.isEmpty()) {
                if (midjourneyService.consumers.size() == 1) {
                    taskOptional = midjourneyService.consumers.stream().findFirst();
                    log.warn("图片生成成功，尝试寻找消息1");
                }
            }


            if (taskOptional.isEmpty()) {
                var midjourneyTaskType = MidjourneyTaskType.typeBySimpleContent(content);
                if (midjourneyTaskType != null) {
                    var typeList = midjourneyService.consumers.stream()
                            .filter(it -> it.getTaskType() == midjourneyTaskType)
                            .toList();
                    if (typeList.size() == 1) {
                        taskOptional = typeList.stream().findFirst();
                        log.warn("图片生成成功，尝试寻找消息2");
                    } else {
                        taskOptional = typeList.stream()
                                .filter(it -> it.similarSimple(content))
                                .findFirst();
                        log.warn("图片生成成功，尝试寻找消息3");
                    }
                }
            }

            if (taskOptional.isEmpty()) {
                taskOptional = midjourneyService.consumers.stream()
                        .filter(it -> it.similarSimple(content))
                        .findFirst();
                log.warn("图片生成成功，尝试寻找消息4");
            }

            if (taskOptional.isEmpty()) {
                taskOptional = midjourneyService.consumers.stream()
                        .filter(it -> it.similarNormal(content))
                        .findFirst();
                log.warn("图片生成成功，尝试寻找消息5");
            }

            if (taskOptional.isEmpty()) {
                taskOptional = midjourneyService.consumers.stream()
                        .filter(it -> it.similarComplex(content))
                        .findFirst();
                log.warn("图片生成成功，尝试寻找消息6");
            }


            if (taskOptional.isEmpty()) {
                // 如果找不到，则说明mj没有通知中间的消息，直接返回了结果，则继续模糊查找prompt
                // type:[DEFAULT] - id:[1136004606929936384] - nonce:[null] - author:[Midjourney Bot] - content:[**<https://s.mj.run/fhGJNSBSzuw> Add a boy** - <@605602758384680972> (fast)] - image:[https://cdn.discordapp.com/attachments/1117348636100136981/1136004606313369710/project_zfoo_Add_a_boy_31f60b54-0774-42de-a3ee-8beff67ae63b.png]
                var prompt1 = StringUtils.substringBeforeLast(imageName, "_");
                var prompt11 = StringUtils.substringAfterLast(prompt1, "project_zfoo_");
                taskOptional = midjourneyService.consumers.stream().filter(it -> it.getPromptWith().replaceAll(" ", "_").contains(prompt11)).findFirst();
                log.warn("图片生成成功，尝试寻找消息7, [{}] [{}]", prompt1, prompt11);
            }

            if (taskOptional.isEmpty()) {
                log.error("图片任务没有找到消息8");
                return;
            }


            var task = taskOptional.get();
            task.setDiscordMessageId(discordMessageId);
            midjourneyService.consumers.remove(task);
            log.info("task is [{}]", JsonUtils.object2String(task));

            // oss的meta
            var pngObjectMetadata = new ObjectMetadata();
            pngObjectMetadata.setContentType("image/png");
            var jpgObjectMetadata = new ObjectMetadata();
            jpgObjectMetadata.setContentType("image/jpg");

            // 上传正常图片到oss
            log.info("download image from discord [{}]", discordImageUrl);
            var bytes = HttpProxyUtils.getBytes(discordImageUrl);
            log.info("start upload image to oss bytes:[{}]", bytes.length);
            var objectName = StringUtils.format("am/{}.png", RandomUtils.randomString(24));
            ossManager.upload(bytes, objectName, pngObjectMetadata);
            // 上传低质量图片到oss
            var lowBytes = ImageUtils.compress(bytes, 0.3F);
            var objectNameLow = StringUtils.format("am/{}.jpg", RandomUtils.randomString(24));
            ossManager.upload(lowBytes, objectNameLow, jpgObjectMetadata);
            // 上传中等质量图片到oss
            var middleBytes = ImageUtils.compress(bytes, 0.5F);
            var objectNameMiddle = StringUtils.format("am/{}.jpg", RandomUtils.randomString(24));
            ossManager.upload(middleBytes, objectNameMiddle, jpgObjectMetadata);
            // 上传高等质量图片到oss
            var highBytes = ImageUtils.compress(bytes, 0.7F);
            var objectNameHigh = StringUtils.format("am/{}.jpg", RandomUtils.randomString(24));
            ossManager.upload(highBytes, objectNameHigh, jpgObjectMetadata);
            log.info("end upload image to oss");

            // 增加历史记录
            var imageUrl = StringUtils.format("https://jiucai.fun/{}", objectName);
            var imageUrlLow = StringUtils.format("https://jiucai.fun/{}", objectNameLow);
            var imageUrlMiddle = StringUtils.format("https://jiucai.fun/{}", objectNameMiddle);
            var imageUrlHigh = StringUtils.format("https://jiucai.fun/{}", objectNameHigh);
            var id = MongoIdUtils.getIncrementIdFromMongoDefault(MidjourneyEntity.class);
            var entity = new MidjourneyEntity(id, discordMessageId, task.getNonce(), task.getPrompt(), imageName, imageRealName, imageUrl, imageUrlLow, imageUrlMiddle, imageUrlHigh, TimeUtils.now());
            OrmContext.getAccessor().insert(entity);
            imageService.imageHistory(imageUrl, imageUrlLow, imageUrlMiddle, imageUrlHigh);
            log.info("[{}] [{}] [{}] [{}] [{}]", imageUrl, imageUrlLow, imageUrlMiddle, imageUrlHigh, task.getPrompt());

            // 转发给客户端
            var notice = MidImagineNotice.valueOf(MidImagineNotice.complete, task.getNonce(), content);
            // 保护原图，对原图做一个Aes加密
            notice.setImageUrl(AesUtils.getEncryptString(imageUrl));
            notice.setImageUrlLow(imageUrlLow);
            notice.setImageUrlMiddle(imageUrlMiddle);
            notice.setImageUrlHigh(AesUtils.getEncryptString(imageUrlHigh));

            if (task.getTaskType() == MidjourneyTaskType.UPSCALE) {
                notice.setUpscale(true);
                if (task.getCategory().contains("variation") || task.getCategory().contains("pan")) {
                    notice.setReroll(true);
                }
            } else if (task.getTaskType() == MidjourneyTaskType.UPSAMPLE) {
                notice.setUpsample(true);
            } else if (task.getTaskType() == MidjourneyTaskType.ZOOM) {
                notice.setReroll(true);
            } else {
                notice.setReroll(true);
            }
            notice.setMidjourneyId(id);
            brokerService.broker(new TransferMidImagineNotify(task.getRequestSid(), notice));

            log.info("midjourney notify {}", JsonUtils.object2String(nonce));
            log.info("midjourney entity {}", JsonUtils.object2String(entity));
        } else {
            // nonce不为空，说明这是由客户端发出的imagine任务
            var taskOptional = midjourneyService.consumers.stream().filter(it -> it.getNonce().equals(nonce)).findFirst();
            if (taskOptional.isEmpty()) {
                return;
            }
            var task = taskOptional.get();
            task.setDiscordMessageId(discordMessageId);
            // 做一个容错，midjourney有时候会改变prompt
            var returnPrompt = StringUtils.substringBeforeLast(content, "-");
            returnPrompt = StringUtils.trim(returnPrompt);
            task.setPrompt(returnPrompt);
            var notice = MidImagineNotice.valueOf(MidImagineNotice.create, task.getNonce(), content);
            brokerService.broker(new TransferMidImagineNotify(task.getRequestSid(), notice));
        }
    }


    protected String getImageUrl(Message message) {
        if (CollectionUtils.isEmpty(message.getAttachments())) {
            return null;
        }
        String imageUrl = message.getAttachments().get(0).getUrl();
        if (StringUtils.isBlank(imageUrl)) {
            return imageUrl;
        }
        if (imageUrl.startsWith(DISCORD_CDN_URL)) {
            return imageUrl;
        }
        return imageUrl.replaceFirst(DISCORD_CDN_URL, DISCORD_CDN_URL);
    }

    private String simpleContent(Message message) {
        var replace = "<@605602758384680972>";
        var content = StringUtils.trim(message.getContentRaw());
        if (content.contains(replace)) {
            content = content.replaceAll(replace, "");
        }
        return content;
    }

    private int progress(Message message) {
        var content = simpleContent(message);
        content = StringUtils.substringAfterLast(content, "-");
        content = StringUtils.substringBeforeFirst(content, "%");
        content = StringUtils.substringAfterFirst(content, "(");
        if (!NumberUtils.isInteger(content)) {
            return 0;
        }
        return Integer.parseInt(content);
    }


}
