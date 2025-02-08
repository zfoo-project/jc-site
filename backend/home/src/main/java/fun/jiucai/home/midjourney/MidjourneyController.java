package fun.jiucai.home.midjourney;

import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.protocol.midjourney.*;
import fun.jiucai.home.manager.TranslateManager;
import fun.jiucai.home.midjourney.model.MidjourneyTask;
import fun.jiucai.home.midjourney.model.MidjourneyTaskType;
import fun.jiucai.home.service.BrokerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 */
@Slf4j
@Component
public class MidjourneyController {

    @Autowired
    private MidjourneyService midjourneyService;
    @Autowired
    private BrokerService brokerService;
    @Autowired
    private TranslateManager translateManager;

    @PacketReceiver
    public void atTransferMidImagineAsk(Session session, TransferMidImagineAsk ask) {
        var nonce = ask.getRequest().getNonce();
        var prompt = ask.getRequest().getPrompt();
        log.info("atTransferMidImagineAsk [nonce:{}] [prompt:{}]", nonce, prompt);

        // 去除开头的/
        if (prompt.startsWith("/")) {
            prompt = StringUtils.substringAfterLast(prompt, "/");
        }

        // 转换成英文
        var suffixParams = "";
        if (translateManager.isContainChinese(prompt) && prompt.contains("--")) {
            suffixParams = StringUtils.substringAfterFirst(prompt, "--");
            prompt = StringUtils.substringBeforeFirst(prompt, "--");
        }
        var enPrompt = translateManager.cn2en(prompt);
        // json字符串中不能包含双引号 "
        enPrompt = enPrompt.replace("\"", "'");
        enPrompt = enPrompt.replace("\\", "");
        if (StringUtils.isNotBlank(suffixParams)) {
            enPrompt = enPrompt + " --" + suffixParams;
        }

        var task = MidjourneyTask.valueOf(MidjourneyTaskType.IMAGINE, ask.getRequestSid(), nonce, enPrompt);
        midjourneyService.providers.offer(task);

        var content = StringUtils.format("**{}** - imagine join provider queue of tasks", enPrompt);
        var notice = MidImagineNotice.valueOf(MidImagineNotice.provider, nonce, content);
        brokerService.broker(new TransferMidImagineNotify(ask.getRequestSid(), notice));
        log.info("[nonce:{}] [prompt:{}] [enPrompt:{}] imagine join provider queue of tasks", nonce, prompt, enPrompt);
    }

    @PacketReceiver
    public void atTransferMidRerollAsk(Session session, TransferMidRerollAsk ask) {
        var requestSid = ask.getRequestSid();
        var nonce = ask.getRequest().getNonce();
        var midjourneyId = ask.getRequest().getMidjourneyId();
        log.info("atTransferMidRerollAsk [requestSid:{}] [nonce:{}] [midjourneyId:{}]", requestSid, nonce, midjourneyId);

        var history = midjourneyService.midjourneyEntityCaches.load(midjourneyId);
        if (history == null) {
            var notice = MidImagineNotice.valueOf(MidImagineNotice.expire, nonce, "imagine reroll expire");
            brokerService.broker(new TransferMidImagineNotify(requestSid, notice));
            log.info("[nonce:{}] [midjourneyId:{}] expire", nonce, midjourneyId);
            return;
        }

        var task = MidjourneyTask.valueOf(MidjourneyTaskType.REROLL, requestSid, nonce, history.getPrompt());
        task.setDiscordMessageId(history.getDiscordMessageId());
        task.setImageRealName(history.getImageRealName());
        midjourneyService.providers.offer(task);

        var content = StringUtils.format("**{}** - reroll join provider queue of tasks", history.getPrompt());
        var notice = MidImagineNotice.valueOf(MidImagineNotice.provider, nonce, content);
        brokerService.broker(new TransferMidImagineNotify(ask.getRequestSid(), notice));
        log.info("[nonce:{}] [prompt:{}] reroll join provider queue of tasks", nonce, history.getPrompt());
    }

    @PacketReceiver
    public void atTransferMidSelectAsk(Session session, TransferMidSelectAsk ask) {
        var requestSid = ask.getRequestSid();
        var category = ask.getRequest().getCategory();
        var index = ask.getRequest().getIndex();
        var nonce = ask.getRequest().getNonce();
        var midjourneyId = ask.getRequest().getMidjourneyId();

        log.info("atTransferMidSelectAsk [requestSid:{}] [category:{}] [index:{}] [nonce:{}] [midjourneyId:{}]", requestSid, category, index, nonce, midjourneyId);

        var history = midjourneyService.midjourneyEntityCaches.load(midjourneyId);
        if (history == null) {
            var notice = MidImagineNotice.valueOf(MidImagineNotice.expire, nonce, "imagine select expire");
            brokerService.broker(new TransferMidImagineNotify(requestSid, notice));
            log.info("[nonce:{}] [midjourneyId:{}] select expire", nonce, midjourneyId);
            return;
        }
        var taskType = category.equals("upsample") ? MidjourneyTaskType.UPSAMPLE : MidjourneyTaskType.VARIATION;

        var task = MidjourneyTask.valueOf(taskType, requestSid, nonce, history.getPrompt());
        task.setDiscordMessageId(history.getDiscordMessageId());
        task.setImageRealName(history.getImageRealName());
        task.setSelectIndex(index);
        task.setCategory(category);
        midjourneyService.providers.offer(task);

        var content = StringUtils.format("**{}** - select join provider queue of tasks", history.getPrompt());
        var notice = MidImagineNotice.valueOf(MidImagineNotice.provider, nonce, content);
        brokerService.broker(new TransferMidImagineNotify(ask.getRequestSid(), notice));
        log.info("[nonce:{}] [prompt:{}] select join provider queue of tasks", nonce, history.getPrompt());
    }

    @PacketReceiver
    public void atTransferMidUpscaleAsk(Session session, TransferMidUpscaleAsk ask) {
        var requestSid = ask.getRequestSid();
        var category = ask.getRequest().getCategory();
        var nonce = ask.getRequest().getNonce();
        var midjourneyId = ask.getRequest().getMidjourneyId();

        log.info("atTransferMidUpscaleAsk [requestSid:{}] [category:{}] [nonce:{}] [midjourneyId:{}]", requestSid, category, nonce, midjourneyId);

        var history = midjourneyService.midjourneyEntityCaches.load(midjourneyId);
        if (history == null) {
            var notice = MidImagineNotice.valueOf(MidImagineNotice.expire, nonce, "imagine upscale expire");
            brokerService.broker(new TransferMidImagineNotify(requestSid, notice));
            log.info("[nonce:{}] [midjourneyId:{}] select expire", nonce, midjourneyId);
            return;
        }
        var taskType = MidjourneyTaskType.UPSCALE;

        var task = MidjourneyTask.valueOf(taskType, requestSid, nonce, history.getPrompt());
        task.setDiscordMessageId(history.getDiscordMessageId());
        task.setImageRealName(history.getImageRealName());
        task.setCategory(category);
        midjourneyService.providers.offer(task);

        var content = StringUtils.format("**{}** - upscale join provider queue of tasks", history.getPrompt());
        var notice = MidImagineNotice.valueOf(MidImagineNotice.provider, nonce, content);
        brokerService.broker(new TransferMidImagineNotify(ask.getRequestSid(), notice));
        log.info("[nonce:{}] [prompt:{}] upscale join provider queue of tasks", nonce, history.getPrompt());
    }

    @PacketReceiver
    public void atTransferMidZoomAsk(Session session, TransferMidZoomAsk ask) {
        var requestSid = ask.getRequestSid();
        var zoom = ask.getRequest().getZoom();
        var nonce = ask.getRequest().getNonce();
        var midjourneyId = ask.getRequest().getMidjourneyId();

        log.info("atTransferMidZoomAsk [requestSid:{}] [zoom:{}] [nonce:{}] [midjourneyId:{}]", requestSid, zoom, nonce, midjourneyId);

        var history = midjourneyService.midjourneyEntityCaches.load(midjourneyId);
        if (history == null) {
            var notice = MidImagineNotice.valueOf(MidImagineNotice.expire, nonce, "imagine zoom expire");
            brokerService.broker(new TransferMidImagineNotify(requestSid, notice));
            log.info("[nonce:{}] [midjourneyId:{}] select expire", nonce, midjourneyId);
            return;
        }
        var taskType = MidjourneyTaskType.ZOOM;

        var task = MidjourneyTask.valueOf(taskType, requestSid, nonce, history.getPrompt());
        task.setDiscordMessageId(history.getDiscordMessageId());
        task.setImageRealName(history.getImageRealName());
        task.setZoom(zoom);
        midjourneyService.providers.offer(task);

        var content = StringUtils.format("**{}** - zoom join provider queue of tasks", history.getPrompt());
        var notice = MidImagineNotice.valueOf(MidImagineNotice.provider, nonce, content);
        brokerService.broker(new TransferMidImagineNotify(ask.getRequestSid(), notice));
        log.info("[nonce:{}] [prompt:{}] atMidZoomAsk join provider queue of tasks", nonce, history.getPrompt());
    }

    @PacketReceiver
    public void atTransferMidInpaintAsk(Session session, TransferMidInpaintAsk ask) {
        var requestSid = ask.getRequestSid();
        var nonce = ask.getRequest().getNonce();
        var midjourneyId = ask.getRequest().getMidjourneyId();

        log.info("atTransferMidInpaintAsk [requestSid:{}] [nonce:{}] [midjourneyId:{}]", requestSid, nonce, midjourneyId);

        var history = midjourneyService.midjourneyEntityCaches.load(midjourneyId);
        if (history == null) {
            var notice = MidImagineNotice.valueOf(MidImagineNotice.expire, nonce, "imagine inpaint expire");
            brokerService.broker(new TransferMidImagineNotify(requestSid, notice));
            log.info("[nonce:{}] [midjourneyId:{}] select expire", nonce, midjourneyId);
            return;
        }
        var taskType = MidjourneyTaskType.ZOOM;

        var task = MidjourneyTask.valueOf(taskType, requestSid, nonce, history.getPrompt());
        task.setDiscordMessageId(history.getDiscordMessageId());
        task.setImageRealName(history.getImageRealName());

        midjourneyService.inpaint(task);
    }

    @Scheduler(cron = "0/1 * * * * ?")
    public void cronConsumer() {
        midjourneyService.refreshConsumers();

        if (CollectionUtils.isEmpty(midjourneyService.providers)) {
            return;
        }

        if (midjourneyService.consumers.size() >= 13) {
            return;
        }

        var task = midjourneyService.providers.poll();
        task.setStartTime(TimeUtils.now());
        midjourneyService.consumers.offer(task);
        log.info("midjourney start task [{}]", JsonUtils.object2String(task));
        switch (task.getTaskType()) {
            case IMAGINE:
                midjourneyService.imagine(task);
                break;
            case REROLL:
                midjourneyService.reroll(task);
                break;
            case UPSAMPLE:
                midjourneyService.upsample(task);
                break;
            case VARIATION:
                midjourneyService.variation(task);
                break;
            case UPSCALE:
                midjourneyService.upscale(task);
                break;
            case ZOOM:
                midjourneyService.zoom(task);
                break;
            default:
        }

        var content = StringUtils.format("**{}** - start consumer task", task.getPrompt());
        var notice = MidImagineNotice.valueOf(MidImagineNotice.consumer, task.getNonce(), content);
        brokerService.broker(new TransferMidImagineNotify(task.getRequestSid(), notice));
    }

    // 每个月15号切换为慢速模式
    @Scheduler(cron = "0 0 0 28 * ?")
    public void cronRelax() {
        midjourneyService.relaxMode();
    }

    // 每个月12号切换为快速模式
    @Scheduler(cron = "0 0 0 12 * ?")
    public void cronFast() {
        midjourneyService.fastMode();
    }
}
