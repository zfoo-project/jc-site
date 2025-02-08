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

import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.entity.midjourney.MidjourneyEntity;
import fun.jiucai.common.protocol.midjourney.TransferMidImagineNotify;
import fun.jiucai.common.protocol.midjourney.MidImagineNotice;
import fun.jiucai.common.util.HttpProxyUtils;
import fun.jiucai.common.util.HttpUtils;
import fun.jiucai.home.config.MyConfiguration;
import fun.jiucai.home.midjourney.model.MidjourneyTask;
import fun.jiucai.home.service.BrokerService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author godotg
 */
@Slf4j
@Component
public class MidjourneyService {

    public static final String DISCORD_URL = "https://discord.com/api/v9/interactions";

    public static final String DISCORD_INPAINT_URL = "https://{}.discordsays.com/inpaint/index.html?instance_id={}&custom_id={}&channel_id={}&guild_id={}&frame_id={}&platform=desktop";
    public static final String DISCORD_INPAINT_URL111 = "https://936929561302675456.discordsays.com/inpaint/index.html?instance_id=1117348636100136981%3A936929561302675456%3AMJ%3A%3Aiframe%3A%3AufMpHAv4fCEOis71loPuGelP6e1Yt7A8tFXwKCNFurouFKtJjDuW2BJ8NZoi6EN6OY9SM1dvK8LVaaaa&custom_id=MJ%3A%3Aiframe%3A%3AufMpHAv4fCEOis71loPuGelP6e1Yt7A8tFXwKCNFurouFKtJjDuW2BJ8NZoi6EN6OY9SM1dvK8LVaaaa&channel_id=1117348636100136981&guild_id=1117348635420655666&frame_id=63833bbd-ce1f-4d0d-8177-22444be05aaa&platform=desktop";

    private static final String imagineJson = ClassUtils.getFileFromClassPathToString("midjourney/imagine.json");
    private static final String rerollJson = ClassUtils.getFileFromClassPathToString("midjourney/reroll.json");
    private static final String upsampleJson = ClassUtils.getFileFromClassPathToString("midjourney/upsample.json");
    private static final String variationJson = ClassUtils.getFileFromClassPathToString("midjourney/variation.json");
    private static final String upscaleJson = ClassUtils.getFileFromClassPathToString("midjourney/upscale.json");
    private static final String zoomJson = ClassUtils.getFileFromClassPathToString("midjourney/zoom.json");
    private static final String inpaintJson = ClassUtils.getFileFromClassPathToString("midjourney/inpaint.json");
    private static final String fastJson = ClassUtils.getFileFromClassPathToString("midjourney/fast.json");
    private static final String relaxJson = ClassUtils.getFileFromClassPathToString("midjourney/relax.json");


    @Autowired
    private MyConfiguration myConfiguration;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private DiscordService discordService;

    public LinkedBlockingQueue<MidjourneyTask> providers = new LinkedBlockingQueue<>(1000);
    public LinkedBlockingQueue<MidjourneyTask> consumers = new LinkedBlockingQueue<>(1000);

    @EntityCacheAutowired
    public IEntityCache<Long, MidjourneyEntity> midjourneyEntityCaches;

    public void refreshConsumers() {
        if (CollectionUtils.isEmpty(consumers)) {
            return;
        }
        var firstTask = consumers.peek();
        var defaultTimeout = 18 * TimeUtils.MILLIS_PER_MINUTE;
        if (TimeUtils.now() - firstTask.getStartTime() < defaultTimeout) {
            return;
        }
        consumers.poll();
        var reasons = new StringBuilder();
        reasons.append(FileUtils.LS);
        reasons.append("```").append(FileUtils.LS);
        reasons.append("1. AI moderators feel your prompt might be against our community standards.(提示词包含不健康的词语)").append(FileUtils.LS);
        reasons.append("2. Drawing requests are too frequent.(作图的请求次数过于频繁)").append(FileUtils.LS);
        reasons.append("3. Change the prompt and try it again.(更换prompt并重新尝试)").append(FileUtils.LS);
        reasons.append("```").append(FileUtils.LS);

        var content = StringUtils.format("**{}** - An error has been encountered, consider the following reasons:{}", firstTask.getPrompt(), reasons.toString());
        var notice = MidImagineNotice.valueOf(MidImagineNotice.stop, firstTask.getNonce(), content);
        brokerService.broker(new TransferMidImagineNotify(firstTask.getRequestSid(), notice));
        log.info("refreshConsumers [prompt:{}] process timed out", firstTask.getPrompt());
    }

    public String simplePrompt(String prompt) {
        prompt = StringUtils.trim(prompt);
        prompt = prompt.replaceAll(FileUtils.WINDOWS_LS, "").replaceAll(FileUtils.UNIX_LS, "");
        return prompt;
    }

    public void imagine(MidjourneyTask task) {
        var prompt = simplePrompt(task.getPrompt());

        // 尝试获取图片的url刷新cdn缓存，https://jiucai.fun
        if (prompt.contains("https://jiucai.fun")) {
           var imageUrl = StringUtils.substringAfterFirst(prompt, "https://jiucai.fun");
           imageUrl = StringUtils.substringBeforeFirst(prompt, StringUtils.SPACE);
            for (int i = 0; i < 3; i++) {
                try {
                    var bytes = HttpUtils.getBytes(imageUrl);
                    if (ArrayUtils.isNotEmpty(bytes)) {
                        var message = discordService.channel()
                                .sendFiles(FileUpload.fromData(new ByteArrayInputStream(bytes), imageUrl))
                                .submit()
                                .get();
                        var attachment = message.getAttachments().get(0);
                        var realImageUrl = attachment.getUrl();
                        prompt = prompt.replaceFirst(imageUrl, realImageUrl);
                        log.info("upload image to discord imageUrl:[{}] realImageUrl:[{}]", imageUrl, realImageUrl);
                        break;
                    }

                } catch (Exception e) {
                    log.error("获取图片url:[{}]数据错误", imageUrl, e);
                }
            }
        }

        var nonce = task.getNonce();
        var json = StringUtils.format(imagineJson, myConfiguration.getMidjourney().getSessionId(), prompt, nonce);
        log.info("imagine [{}]", json);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("imagine midjourney prompt success response:[{}]", response);
        } else {
            log.error("imagine midjourney prompt error:[{}]", response);
        }
    }

    public void reroll(MidjourneyTask task) {
        var prompt = simplePrompt(task.getPrompt());
        var nonce = task.getNonce();
        var json = StringUtils.format(rerollJson, nonce, task.getDiscordMessageId(), myConfiguration.getMidjourney().getSessionId(), task.getImageRealName());
        log.info("reroll [{}]", json);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("reroll midjourney prompt success response:[{}]", response);
        } else {
            log.error("reroll midjourney prompt error:[{}]", response);
        }
    }

    public void upsample(MidjourneyTask task) {
        var prompt = simplePrompt(task.getPrompt());
        var nonce = task.getNonce();
        var json = StringUtils.format(upsampleJson, nonce, task.getDiscordMessageId(), myConfiguration.getMidjourney().getSessionId(), task.getSelectIndex(), task.getImageRealName());
        log.info("upsample [{}]", json);
        task.setPromptWith(StringUtils.format("{} - Image #{}", task.getPrompt(), task.getSelectIndex()));
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("upsample midjourney prompt success response:[{}]", response);
        } else {
            log.error("upsample midjourney prompt error:[{}]", response);
        }
    }

    public void variation(MidjourneyTask task) {
        var prompt = simplePrompt(task.getPrompt());
        var nonce = task.getNonce();
        var json = StringUtils.format(variationJson, nonce, task.getDiscordMessageId(), myConfiguration.getMidjourney().getSessionId(), task.getSelectIndex(), task.getImageRealName());
        log.info("variation [{}]", json);
        task.setPromptWith(task.getPrompt() + " - Variations");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("variation midjourney prompt success response:[{}]", response);
        } else {
            log.error("variation midjourney prompt error:[{}]", response);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void upscale(MidjourneyTask task) {
        var prompt = simplePrompt(task.getPrompt());
        var nonce = task.getNonce();
        var json = StringUtils.format(upscaleJson, nonce, task.getDiscordMessageId(), myConfiguration.getMidjourney().getSessionId(), task.getCategory(), task.getImageRealName());
        log.info("upscale [{}]", json);
        task.setPromptWith(StringUtils.format("{} - upscale", task.getPrompt(), task.getSelectIndex()));
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("upscale midjourney prompt success response:[{}]", response);
        } else {
            log.error("upscale midjourney prompt error:[{}]", response);
        }
    }

    public void zoom(MidjourneyTask task) {
        var prompt = simplePrompt(task.getPrompt());
        var nonce = task.getNonce();
        var json = StringUtils.format(zoomJson, nonce, task.getDiscordMessageId(), myConfiguration.getMidjourney().getSessionId(), task.getZoom(), task.getImageRealName());
        log.info("zoom [{}]", json);
        task.setPromptWith(StringUtils.format("{} - zoom", task.getPrompt(), task.getSelectIndex()));
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("upscale midjourney prompt success response:[{}]", response);
        } else {
            log.error("upscale midjourney prompt error:[{}]", response);
        }
    }

    public void inpaint(MidjourneyTask task) {
        var prompt = simplePrompt(task.getPrompt());
        var nonce = task.getNonce();
        var json = StringUtils.format(inpaintJson, nonce, task.getDiscordMessageId(), myConfiguration.getMidjourney().getSessionId(), task.getImageRealName());
        log.info("inpaint [{}]", json);
        task.setPromptWith(StringUtils.format("{} - zoom", task.getPrompt(), task.getSelectIndex()));
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("upscale midjourney prompt success response:[{}]", response);
        } else {
            log.error("upscale midjourney prompt error:[{}]", response);
        }
    }

    private String nonce() {
        var a = RandomUtils.randomInt(10_0000_0000, 20_0000_0000);
        var b = RandomUtils.randomInt(10_0000_0000, 20_0000_0000);
        return String.valueOf(a) + String.valueOf(b);
    }

    public void fastMode() {
        var nonce = nonce();
        var json = StringUtils.format(fastJson, myConfiguration.getMidjourney().getSessionId(), nonce);
        log.info("fastMode [{}]", json);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("fastMode midjourney success response:[{}]", response);
        } else {
            log.error("fastMode midjourney error:[{}]", response);
        }
    }

    public void relaxMode() {
        var nonce = nonce();
        var json = StringUtils.format(relaxJson, myConfiguration.getMidjourney().getSessionId(), nonce);
        log.info("relaxMode [{}]", json);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", myConfiguration.getMidjourney().getUserToken());
        var response = HttpProxyUtils.postWithString(DISCORD_URL, json, headers);
        if (response == null) {
            log.info("relaxMode midjourney success response:[{}]", response);
        } else {
            log.error("relaxMode midjourney error:[{}]", response);
        }
    }
}
