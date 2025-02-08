package fun.jiucai.cloud.stock;

import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.scheduler.util.SingleCache;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.anno.StorageAutowired;
import com.zfoo.storage.model.IStorage;
import fun.jiucai.cloud.resource.stock.CoreResource;
import fun.jiucai.cloud.stock.util.EastMoneyUtils;
import fun.jiucai.cloud.stock.util.ThsUtils;
import fun.jiucai.common.entity.StockRankEntity;
import fun.jiucai.common.entity.UserEntity;
import fun.jiucai.common.protocol.rank.EastMoneyRank;
import fun.jiucai.common.protocol.rank.RankRequest;
import fun.jiucai.common.protocol.rank.RankResponse;
import fun.jiucai.common.protocol.rank.ThsRank;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 */
@Component
public class Rank100Controller implements ApplicationListener<AppStartEvent> {

    @EntityCacheAutowired
    private IEntityCache<Integer, StockRankEntity> stockRankEntityCaches;

    @StorageAutowired
    private IStorage<Integer, CoreResource> coreStorage;

    private SingleCache<List<EastMoneyRank>> eastMoneyRankCache;
    private SingleCache<List<ThsRank>> thsRankCache;


    @Override
    public void onApplicationEvent(AppStartEvent event) {
        eastMoneyRankCache = SingleCache.build(60 * TimeUtils.MILLIS_PER_MINUTE, () -> {
            var list = EastMoneyUtils.rank100();
            for (EastMoneyRank rank : list) {
                var entity = stockRankEntityCaches.loadOrCreate(rank.getCode());
                rank.setPrimary(entity.primaryRankThs());
                stockRankEntityCaches.update(entity);
            }
            return list;
        });
        thsRankCache = SingleCache.build(60 * TimeUtils.MILLIS_PER_MINUTE, () -> {
            var ranks = ThsUtils.rank100();
            var list = new ArrayList<ThsRank>();
            for (var rank : ranks) {
                var entity = stockRankEntityCaches.loadOrCreate(rank.getCode());
                var thsRank = new ThsRank(rank.getCode(), rank.getName(), (int) rank.getRate(), rank.getRankChange(), rank.getAnalyse(), entity.primaryRankEastMoney());
                list.add(thsRank);
                stockRankEntityCaches.update(entity);
            }
            return list;
        });
    }

    @PacketReceiver
    public void atRankRequest(Session session, RankRequest request) {
        var coreResource = RandomUtils.randomEle(coreStorage.getIndexes(CoreResource::getType, 2));
        if (RandomUtils.randomBoolean()) {
            coreResource = RandomUtils.randomEle(coreStorage.getIndexes(CoreResource::getType, 9));
        }

        var num = request.getNum();
        var eastMoneyRanks = eastMoneyRankCache.get().stream().limit(num).toList();
        var thsRanks = thsRankCache.get().stream().limit(num).toList();
        NetContext.getRouter().send(session, new RankResponse(eastMoneyRanks, thsRanks, coreResource.getWord()));
    }

}
