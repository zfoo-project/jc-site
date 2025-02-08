package fun.jiucai.cloud.statistic;

import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.entity.HistoryEntity;
import fun.jiucai.common.entity.StatisticsEntity;
import org.springframework.stereotype.Component;

@Component
public class StatisticsService {

    @EntityCacheAutowired
    private IEntityCache<String, HistoryEntity> historyEntityCaches;
    @EntityCacheAutowired
    private IEntityCache<Long, StatisticsEntity> statisticsEntityCaches;

    public StatisticsEntity loadCurrentStatEntity() {
        return statisticsEntityCaches.loadOrCreate(TimeUtils.getLastTimeOfDay(TimeUtils.now()));
    }

    public void refreshStatistics() {
        var historyEntity = historyEntityCaches.load(HistoryEntity.IP_HISTORY_ID);
        var ips = historyEntity.getHistory().size();

        var statEntity = loadCurrentStatEntity();
        statEntity.setIps(ips);
        statisticsEntityCaches.updateUnsafeNow(statEntity);
    }

}
