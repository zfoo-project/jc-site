package fun.jiucai.cloud.stock;

import com.zfoo.event.manager.EventBus;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.stock.event.GnEvent;
import fun.jiucai.cloud.stock.util.ThsUtils;
import fun.jiucai.common.entity.ConceptEntity;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ConceptThsController {

    private Set<String> thsConcepts = new HashSet<>();

    @PostConstruct
    public void init() {
        EventBus.asyncExecute(() -> cronNewThsGn());
    }

    @SneakyThrows
    @Scheduler(cron = "0 0/10 * * * ?")
    public void cronNewThsGn() {
        var list = ThsUtils.allConcepts();

        // 第一次先初始化
        if (CollectionUtils.isEmpty(thsConcepts)) {
            for (var concept : list) {
                thsConcepts.add(StringUtils.trim(concept.getValue()));
            }
            return;
        }

        for (var concept : list) {
            var conceptName = StringUtils.trim(concept.getValue());

            if (thsConcepts.contains(conceptName)) {
                continue;
            }

            thsConcepts.add(conceptName);

            var conceptId = concept.getKey();
            var name = concept.getValue();
            var title = StringUtils.format("{} {} ", conceptId, name);
            var url = ThsUtils.conceptHtmlUrl(conceptId);
            var gnEntity = new ConceptEntity(MongoIdUtils.getIncrementIdFromMongoDefault(ConceptEntity.class), "同花顺", title, url, TimeUtils.now());
            OrmContext.getAccessor().insert(gnEntity);
            EventBus.post(new GnEvent(gnEntity));
        }
    }


}
