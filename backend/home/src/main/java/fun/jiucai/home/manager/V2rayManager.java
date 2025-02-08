package fun.jiucai.home.manager;

import com.sun.jna.Platform;
import com.zfoo.event.model.AppStartEvent;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.protocol.util.ThreadUtils;
import com.zfoo.scheduler.anno.Scheduler;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.entity.KeyValueEntity;
import fun.jiucai.common.util.HttpProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * @author godotg
 */
@Slf4j
@Component
public class V2rayManager implements ApplicationListener<AppStartEvent>, Ordered {

    public static final String v2ray_configs = "/v2ray";
    public static final String v2ray_config_path = "/usr/local/etc/v2ray";

    @EntityCacheAutowired
    private IEntityCache<String, KeyValueEntity> keyValueEntityCaches;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        SchedulerBus.schedule(() -> cronV2ray(), 3, TimeUnit.SECONDS);
    }


    @Scheduler(cron = "33 0/5 * * * ?")
    public void cronV2ray() {
        if (!Platform.isLinux()) {
            return;
        }

        var v2rayConfigs = FileUtils.getAllReadableFiles(new File(v2ray_configs))
                .stream()
                .filter(it -> it.getName().endsWith(".json"))
                .sorted(Comparator.comparing(File::getName))
                .toList();

        if (CollectionUtils.isEmpty(v2rayConfigs)) {
            log.warn("v2ray has no config files and can not start");
            return;
        }

        if (HttpProxyUtils.isCrossFireWall()) {
            log.info("v2ray can cross fire wall");
            return;
        }

        // 如果网络不可用，则切换v2ray的配置
        var v2rayConfigSize = v2rayConfigs.size();

        var v2rayConfigEntity = keyValueEntityCaches.load(KeyValueEntity.v2ray_config_id);
        var value = v2rayConfigEntity.getValue();

        var currentV2rayId = 0;
        if (!StringUtils.isBlank(value)) {
            currentV2rayId = Integer.parseInt(value);
            // 使用下一个v2ray的配置
            currentV2rayId++;
        }

        v2rayConfigEntity.setValue(String.valueOf(currentV2rayId));
        keyValueEntityCaches.update(v2rayConfigEntity);

        // 获取v2ray的配置，并将这个配置复制到
        var v2rayConfigAbsolutePath = StringUtils.format("{}/config.json", v2ray_config_path);
        FileUtils.deleteFile(new File(v2rayConfigAbsolutePath));

        var index = Math.abs(currentV2rayId % v2rayConfigSize);
        var newConfigFile = v2rayConfigs.get(index);
        var newConfig = FileUtils.readFileToString(newConfigFile);
        FileUtils.writeStringToFile(new File(v2rayConfigAbsolutePath), newConfig, false);

        // 重新启动v2ray
        var result = OSUtils.execCommand("systemctl restart v2ray");
        log.info("v2ray restart and find [{}] configs", v2rayConfigs.size());

        // 延迟3秒执行网络检测
        ThreadUtils.sleep(3 * TimeUtils.MILLIS_PER_SECOND);
        if (HttpProxyUtils.isCrossFireWall()) {
            log.warn("v2ray 切换成功 当前配置[{}.json][result:{}]", currentV2rayId, result);
        } else {
            log.error("v2ray 切换失败[result:{}]，延迟10秒重新执行网络检测", result);
            SchedulerBus.schedule(() -> cronV2ray(), 10, TimeUnit.SECONDS);
        }
    }

}
