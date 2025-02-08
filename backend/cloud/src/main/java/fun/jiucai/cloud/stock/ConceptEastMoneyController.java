package fun.jiucai.cloud.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zfoo.event.manager.EventBus;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.stock.event.GnEvent;
import fun.jiucai.cloud.stock.util.EastMoneyUtils;
import fun.jiucai.common.entity.ConceptEntity;
import fun.jiucai.common.util.HttpUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author godotg
 */
@Component
public class ConceptEastMoneyController {

    private Set<String> eastMoneyConcepts = new HashSet<>();

    @PostConstruct
    public void init() {
        EventBus.asyncExecute(() -> cronNewEastMoneyGn());
    }

    @SneakyThrows
    @Scheduler(cron = "0 0/10 * * * ?")
    public void cronNewEastMoneyGn() {
        var list = EastMoneyUtils.allConcepts();

        // 第一次先初始化
        if (CollectionUtils.isEmpty(eastMoneyConcepts)) {
            for (var concept : list) {
                eastMoneyConcepts.add(StringUtils.trim(concept.getConcept()));
            }
            return;
        }

        for (var concept : list) {
            var conceptName = StringUtils.trim(concept.getConcept());

            if (eastMoneyConcepts.contains(conceptName)) {
                continue;
            }

            eastMoneyConcepts.add(conceptName);

            var title = StringUtils.format("{} {} ", concept.getF12(), conceptName);
            var url = StringUtils.format("http://quote.eastmoney.com/bk/{}.{}.html", concept.getF13(), concept.getF12());

            var gnEntity = new ConceptEntity(MongoIdUtils.getIncrementIdFromMongoDefault(ConceptEntity.class), "东方财富", title, url, TimeUtils.now());
            OrmContext.getAccessor().insert(gnEntity);
            EventBus.post(new GnEvent(gnEntity));
        }
    }



}
