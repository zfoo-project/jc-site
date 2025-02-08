package fun.jiucai.home.sdiffusion;

import com.aliyun.oss.model.ObjectMetadata;
import com.mongodb.ReadPreference;
import com.mongodb.client.model.Filters;
import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.security.AesUtils;
import com.zfoo.net.util.security.MD5Utils;
import com.zfoo.orm.OrmContext;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.constant.SdDimensionEnum;
import fun.jiucai.common.constant.SdiffusionUtils;
import fun.jiucai.common.entity.SdImageEntity;
import fun.jiucai.home.manager.OssManager;
import fun.jiucai.home.manager.TranslateManager;
import fun.jiucai.common.protocol.sdiffusion.SdSimulateAnswer;
import fun.jiucai.common.protocol.sdiffusion.SdSimulateAsk;
import fun.jiucai.common.protocol.sdiffusion.TransferSdSimulateNotify;
import fun.jiucai.common.protocol.sdiffusion.SdImage;
import fun.jiucai.common.protocol.sdiffusion.SdSimulateNotice;
import fun.jiucai.common.protocol.sdiffusion.SdSimulateResponse;
import fun.jiucai.home.service.BrokerService;
import fun.jiucai.home.service.ImageService;
import fun.jiucai.home.util.ImageUtils;
import fun.jiucai.home.util.NlpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author godotg
 */
@Slf4j
@Component
public class StableDiffusionController {

    @Autowired
    private BrokerService brokerService;
    @Autowired
    private TranslateManager translateManager;
    @Autowired
    private OssManager ossManager;
    @Autowired
    private ImageService imageService;

    @PacketReceiver
    public void atSdSimulateAsk(Session session, SdSimulateAsk ask) {
        var requestSid = ask.getRequestSid();
        var request = ask.getRequest();
        var nonce = request.getNonce();
        var prompt = request.getPrompt();
        var negativePrompt = request.getNegativePrompt();
        var steps = request.getSteps();
        var batchSize = request.getBatchSize();
        var style = request.getStyle();
        var dimension = request.getDimension();
        var ignores = request.getIgnores();


        // 计算花费的时间
        var averageTime = Math.max(1, steps / 20) * TimeUtils.MILLIS_PER_SECOND;
        var costTime = averageTime * batchSize;

        log.info("[nonce:{}] [prompt:{}] [negativePrompt:{}] start process [averageTime:{}]", nonce, prompt, negativePrompt, averageTime);
        var enPrompt = translateManager.cn2en(prompt);
        EventBus.asyncExecute(() -> {
            // 全文搜索图片
            var searchResults = search(enPrompt, negativePrompt, ignores, style, dimension, SdiffusionUtils.comfortableStep(steps));
            // 上传图片到oss
            var images = searchResults.stream().limit(batchSize).collect(Collectors.toList());
            var sdImages = uploadImage(images, prompt);
            for (int i = 1; i <= sdImages.size(); i++) {
                var delaySdImages = sdImages.stream().limit(i).collect(Collectors.toList());
                var notify = new TransferSdSimulateNotify(ask.getRequestSid(), new SdSimulateNotice(nonce, delaySdImages));
                SchedulerBus.schedule(() -> brokerService.broker(notify), averageTime * i, TimeUnit.MILLISECONDS);
            }
        });

        var answer = new SdSimulateAnswer(requestSid, ask.getAttachment(), new SdSimulateResponse(nonce, costTime + batchSize * TimeUtils.MILLIS_PER_SECOND, enPrompt));
        NetContext.getRouter().send(session, answer);
    }


    // -------------------------------------------------------------------------------------------------------------------
    public static final int MAX_BATCH_SIZE = 12;
    public static final Set<String> girlSet = Set.of("girl", "girls", "girlish", "girlfriend", "woman", "female", "feminine"
            , "womanhood", "maid", "maiden", "popsie", "daughter", "bride", "lass");

    public List<SdImageEntity> search(String prompt, String negativePrompt, List<Long> ignores, int style, int dimension, int step) {
        log.info("[{}] start search", prompt);
        // 转换成英文
        var enPrompt = translateManager.cn2en(prompt);
        var enNegativePrompt = translateManager.cn2en(negativePrompt);

        // 分词并去除不重要的词
        var promptKeywords = NlpUtils.extractKeywords(enPrompt);
        var negativePromptKeywords = NlpUtils.extractKeywords(enNegativePrompt);

        // 过滤掉girl，women等关键词，因为默认就是girl
        promptKeywords = promptKeywords.stream()
                .map(it -> StringUtils.trim(it))
                .map(it -> it.toLowerCase())
                .filter(it -> !girlSet.contains(it))
                .collect(Collectors.toList());

        // 使用MongoDB的全文搜索
        var searchFullBuilder = new StringBuilder();
        var searchPromptBuilder = new StringBuilder();
        var searchNegativePromptBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(promptKeywords)) {
            for (var keyword : promptKeywords) {
                searchPromptBuilder.append(StringUtils.format("\"{}\" ", keyword));
            }
        }
        if (CollectionUtils.isNotEmpty(negativePromptKeywords)) {
            for (var keyword : negativePromptKeywords) {
                searchNegativePromptBuilder.append(StringUtils.format("-\"{}\" ", keyword));
            }
        }

        searchFullBuilder.append(searchPromptBuilder).append(searchNegativePromptBuilder);
        var searchFull = searchFullBuilder.toString().trim();
        var searchPrompt = searchFullBuilder.toString().trim();
        var searchNegativePrompt = searchFullBuilder.toString().trim();

        var set = new HashSet<SdImageEntity>();
        var stepFilters = Filters.eq("step", step);
        var styleFilters = Filters.eq("style", style);
        var dimensionFilters = SdDimensionEnum.sdDimension(dimension) == SdDimensionEnum.DIMENSION_RANDOM
                ? Filters.empty()
                : Filters.eq("dimension", dimension);
        var ignoresFilters = Filters.nin("_id", ignores);
        if (StringUtils.isNotEmpty(searchFull)) {
            set.addAll(searchRandom(Filters.text(searchFull), styleFilters, dimensionFilters, ignoresFilters));
            log.info("[{}] search 1", prompt);
            // 关键词优先，舍弃step，需要至少生成4张图片
            if (set.size() < MAX_BATCH_SIZE) {
                set.addAll(searchRandom(Filters.text(searchFull), styleFilters, dimensionFilters, ignoresFilters));
                log.info("[{}] search 2", prompt);
            }

            // 缩短关键词，优先使用前面的关键词搜索
            for (int i = 0; i < promptKeywords.size(); i++) {
                if (set.size() >= MAX_BATCH_SIZE) {
                    break;
                }
                var builder = new StringBuilder();
                var simplePromptKeywords = promptKeywords.stream().limit(promptKeywords.size() - i).collect(Collectors.toList());
                for (var keyword : simplePromptKeywords) {
                    builder.append(StringUtils.format("\"{}\" ", keyword));
                }
                var simplePrompt = builder.toString().trim();
                set.addAll(searchRandom(Filters.text(simplePrompt), styleFilters, dimensionFilters, ignoresFilters));
                set.addAll(searchRandom(Filters.text(simplePrompt), styleFilters, dimensionFilters, ignoresFilters));
                log.info("[{}] [{}] search 3", prompt, simplePrompt);
            }

            // 单个关键词搜索
            for (var keyword : promptKeywords) {
                if (set.size() >= MAX_BATCH_SIZE) {
                    break;
                }
                set.addAll(searchRandom(Filters.text(keyword), styleFilters, dimensionFilters, ignoresFilters));
                set.addAll(searchRandom(Filters.text(keyword), styleFilters, dimensionFilters, ignoresFilters));
                log.info("[{} [{}] search 4", prompt, keyword);
            }
        }

        // 如果找不到，则随机返回
        if (set.size() < MAX_BATCH_SIZE) {
            set.addAll(searchRandom(styleFilters, dimensionFilters, ignoresFilters));
            log.info("[{}] search 5", prompt);
        }

        // 逐渐放宽约束条件
        if (set.size() < MAX_BATCH_SIZE) {
            set.addAll(searchRandom(styleFilters, dimensionFilters, ignoresFilters));
            log.info("[{}] search 6", prompt);
        }
        log.info("[{}] end search with result:[{}]", prompt, set.size());
        return new ArrayList<>(set);
    }

    public List<SdImageEntity> searchRandom(Bson... filters) {
        var collection = OrmContext.getOrmManager().getCollection(SdImageEntity.class)
                .withReadPreference(ReadPreference.secondary());
        var andFilters = Filters.and(filters);
        var count = collection.countDocuments(Filters.and(andFilters));
        var list = new ArrayList<SdImageEntity>();
        collection.find(andFilters)
                .skip(RandomUtils.randomInt(Math.max((int) count - 4, 1)))
                .limit(MAX_BATCH_SIZE)
                .forEach(it -> list.add(it));
        Collections.shuffle(list);
        return list;
    }

    // -------------------------------------------------------------------------------------------------------------------
    @SneakyThrows
    public List<SdImage> uploadImage(List<SdImageEntity> images, String prompt) {
        var list = new ArrayList<SdImage>();
        // oss的meta
        var pngObjectMetadata = new ObjectMetadata();
        pngObjectMetadata.setContentType("image/png");
        var jpgObjectMetadata = new ObjectMetadata();
        jpgObjectMetadata.setContentType("image/jpg");
        for (var image : images) {
            // 上传正常图片到oss
            var imageFile = new File(image.getPath());
            if (!imageFile.exists()) {
                log.warn("image:[{}] does not exist", image.getPath());
                continue;
            }
            var bytes = IOUtils.toByteArray(FileUtils.openInputStream(imageFile));
            var objectName = StringUtils.format("am/{}.png", MD5Utils.strToMD5("normal-quality-" + image.getId()));
            ossManager.uploadIfAbsent(bytes, objectName, pngObjectMetadata);
            // 上传低质量图片到oss
            var lowBytes = ImageUtils.compress(bytes, 0.4F);
            var objectNameLow = StringUtils.format("am/{}.jpg", MD5Utils.strToMD5("low-quality-" + image.getId()));
            ossManager.uploadIfAbsent(lowBytes, objectNameLow, jpgObjectMetadata);
            // 上传中等质量图片到oss
            var middleBytes = ImageUtils.compress(bytes, 0.7F);
            var objectNameMiddle = StringUtils.format("am/{}.jpg", MD5Utils.strToMD5("middle-quality-" + image.getId()));
            ossManager.uploadIfAbsent(middleBytes, objectNameMiddle, jpgObjectMetadata);
            // 上传高等质量图片到oss
            var highBytes = ImageUtils.compress(bytes, 0.9F);
            var objectNameHigh = StringUtils.format("am/{}.jpg", MD5Utils.strToMD5("high-quality-" + image.getId()));
            ossManager.uploadIfAbsent(highBytes, objectNameHigh, jpgObjectMetadata);

            // 增加历史记录
            var imageUrl = StringUtils.format("https://jiucai.fun/{}", objectName);
            var imageUrlLow = StringUtils.format("https://jiucai.fun/{}", objectNameLow);
            var imageUrlMiddle = StringUtils.format("https://jiucai.fun/{}", objectNameMiddle);
            var imageUrlHigh = StringUtils.format("https://jiucai.fun/{}", objectNameHigh);

            var sdImage = new SdImage();
            sdImage.setId(image.getId());
            sdImage.setImageUrl(AesUtils.getEncryptString(imageUrl));
            sdImage.setImageUrlLow(imageUrlLow);
            sdImage.setImageUrlMiddle(imageUrlMiddle);
            sdImage.setImageUrlHigh(imageUrlHigh);
            list.add(sdImage);
            imageService.imageHistory(imageUrl, imageUrlLow, imageUrlMiddle, imageUrlHigh);
            log.info("[{}] [{}] [{}] [{}] [{}]", imageUrl, imageUrlLow, imageUrlMiddle, imageUrlHigh, prompt);
        }
        log.info("upload image count [{}]", list.size());
        return list;
    }


    // -------------------------------------------------------------------------------------------------------------------
    public long costTime(int steps, int batchSize, int dimension) {
        // 4090每秒钟可以计算的面积大小
        var imageCalculatePerSeconds = 512 * 512 * 20;
        var dimensionEnum = SdDimensionEnum.sdDimension(dimension);
        var imageArena = dimensionEnum.getArea();
        var totalTime = (long) imageArena * steps * batchSize / imageCalculatePerSeconds * TimeUtils.MILLIS_PER_SECOND;
        return totalTime;
    }

}
