package fun.jiucai.cloud.stock.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.zfoo.event.anno.EventReceiver;
import com.zfoo.event.model.AppStartEvent;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.anno.Scheduler;
import fun.jiucai.cloud.broker.BrokerService;
import fun.jiucai.cloud.stock.event.GnEvent;
import fun.jiucai.cloud.stock.event.NewsEvent;
import fun.jiucai.cloud.stock.event.NewsLevelEnum;
import fun.jiucai.cloud.wechat.WeChatService;
import fun.jiucai.cloud.wechat.model.WeChatBroadcastRequest;
import fun.jiucai.cloud.wechat.model.WeChatBroadcastResponse;
import fun.jiucai.common.entity.BroadcastEntity;
import fun.jiucai.common.entity.UserEntity;
import fun.jiucai.common.protocol.admin.Broadcast;
import fun.jiucai.common.util.HttpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BroadcastService implements ApplicationListener<AppStartEvent> {

    @Value("${myspring.profiles.active}")
    private String profile;
    @Value("${jiucai.aliyun.accessKeyId}")
    private String aliyunAccessKeyId;
    @Value("${jiucai.aliyun.accessKeySecret}")
    private String aliyunaAccessKeySecret;

    @Autowired
    private WeChatService weChatService;
    @Autowired
    private BrokerService brokerService;
    private Client ssmClient;

    @SneakyThrows
    @Override
    public void onApplicationEvent(AppStartEvent event) {
        if ("dev".equals(profile)) {
            addBroadcast(StringUtils.format("广播测试 - {}", RandomUtils.randomString(6)));
        }

        var config = new Config().setAccessKeyId(aliyunAccessKeyId).setAccessKeySecret(aliyunaAccessKeySecret);
        config.endpoint = "dysmsapi.aliyuncs.com";
        ssmClient = new Client(config);
    }

    @EventReceiver
    public void onNewsEvent(NewsEvent event) {
        var newsEntity = event.getNewsEntity();
        var newsLevel = NewsLevelEnum.newsLevelOfType(newsEntity.getLevel());
        if (newsLevel != NewsLevelEnum.S) {
            return;
        }
        var entity = addBroadcast(StringUtils.format("S级情报 - {}", newsEntity.getTitle()));
        doBroadcastBySsm(entity);
    }

    @EventReceiver
    public void onGnEvent(GnEvent event) {
        var gnEntity = event.getConceptEntity();
        var entity = addBroadcast(StringUtils.format("新概念-{} - {}", gnEntity.getTitle(), gnEntity.getContent()));
        doBroadcastBySsm(entity);
    }

    // 每个小时都通知一下管理员
    @Scheduler(cron = "0 0 * * * ?")
    public void cronBroadcast() {
        if ("dev".equals(profile)) {
            return;
        }
        var allBroadcast = allBroadcast();
        if (CollectionUtils.isEmpty(allBroadcast)) {
            return;
        }
        sendSsm(15300503750L);
        log.info("短信通知管理员有需要广播的消息 size:[{}]", allBroadcast.size());
    }


    public BroadcastEntity addBroadcast(String content) {
        var id = MongoIdUtils.getIncrementIdFromMongoDefault(BroadcastEntity.class);
        var entity = new BroadcastEntity(id, content, null, null);
        OrmContext.getAccessor().insert(entity);
        return entity;
    }

    public List<Broadcast> allBroadcast() {
        var broadcasts = OrmContext.getQuery(BroadcastEntity.class).queryAll().stream().map(it -> it.toBroadcast()).toList();
        return broadcasts;
    }

    public void doBroadcast(long id, String type) throws Exception {
        var entity = OrmContext.getAccessor().load(id, BroadcastEntity.class);
        log.info("广播[{}] - {} - {}", type, id, entity.getContent());
        if ("sms".equals(type)) {
            doBroadcastBySsm(entity);
        } else if ("wechat".equals(type)) {
            doBroadcastByWeChat(entity);
        }
    }

    private void doBroadcastByWeChat(BroadcastEntity entity) throws Exception {
        if ("dev".equals(profile)) {
            entity.setWeChatResult("微信广播成功返回了");
            OrmContext.getAccessor().update(entity);
            return;
        }

        var content = entity.getContent();
        var filter = new WeChatBroadcastRequest.Filter(true);
        var text = new WeChatBroadcastRequest.Text(content);
        var broadcastRequest = new WeChatBroadcastRequest(filter, text, "text");

        var url = StringUtils.format("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token={}", weChatService.accessTokenCache.get());
        var jsonResponse = HttpUtils.post(url, broadcastRequest);
        log.info("微信公众号广播 url:[{}] response:[{}]", url, jsonResponse);
        // 将微信的返回结果入库，便于观察是否发送成功
        entity.setWeChatResult(jsonResponse);
        OrmContext.getAccessor().update(entity);

        var response = JsonUtils.string2Object(jsonResponse, WeChatBroadcastResponse.class);
    }


    // -----------------------------------------------------------------------------------------------------------------
    private void doBroadcastBySsm(BroadcastEntity entity) {
        // 获取所有用户的手机号
        var users = OrmContext.getQuery(UserEntity.class).queryAll();
        var phoneNumbers = users.stream()
                .map(it -> it.getPhoneNumber())
                .filter(it -> it > 0)
                .distinct()
                .toList();

        var success = 0;
        var fail = 0;
        for (var phoneNumber : phoneNumbers) {
            if (sendSsm(phoneNumber)) {
                success++;
            } else {
                fail++;
            }
        }

        entity.setSmsResult(StringUtils.format("total:[{}] success:[{}] fail:[{}]", phoneNumbers.size(), success, fail));
        OrmContext.getAccessor().update(entity);
    }

    private boolean sendSsm(long phoneNumber) {
        try {
            var sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                    .setPhoneNumbers(String.valueOf(phoneNumber))
                    .setSignName("浊浮zfoo")
                    .setTemplateCode("SMS_474445110");
            // 复制代码运行请自行打印 API 的返回值
            var response = ssmClient.sendSmsWithOptions(sendSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());
            log.info("sendSsm phone:[{}] response:[{}]", phoneNumber, JsonUtils.object2String(response.getBody()));
            if (response.getBody() != null && StringUtils.trim(response.getBody().getMessage()).toLowerCase().contains("ok")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("sendSsm error", e);
        }
        return false;
    }

}
