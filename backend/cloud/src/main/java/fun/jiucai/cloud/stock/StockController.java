package fun.jiucai.cloud.stock;

import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.anno.Task;
import com.zfoo.net.session.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.SingleCache;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.entity.MarketEntity;
import fun.jiucai.common.protocol.stock.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StockController implements ApplicationListener<AppStartEvent> {

    private SingleCache<List<Market>> marketCache;

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        marketCache = SingleCache.build(180 * TimeUtils.MILLIS_PER_SECOND, () -> {
            var markets = OrmContext.getQuery(MarketEntity.class).queryAll().stream()
                    .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                    .limit(360)
                    .map(it -> it.toMarket())
                    .toList();
            return markets;
        });
    }


    @PacketReceiver(Task.EventBus)
    public void atSaveMarketAsk(Session session, SaveMarketAsk ask) {
        var markets = ask.getMarkets();

        var collection = OrmContext.getOrmManager().getCollection(MarketEntity.class);
        collection.drop();

        var count = 0;
        for (var market : markets) {
            var id = ++count;

            var m = new MarketEntity(id, market.getDate(), market.getStockNum(), market.getStockNum0(), market.getStockNumNeg005()
                    , market.getStockNumNeg10(), market.getTotalPrice()
                    , market.getMarketIndex(), market.getShMarketIndex(), market.getKcMarketIndex()
                    , market.getSzMarketIndex(), market.getCyMarketIndex(), market.getBjMarketIndex()
                    , market.getExchange(), market.getAmount()
                    , market.getShExchange(), market.getShAmount()
                    , market.getKcExchange(), market.getKcAmount()
                    , market.getSzExchange(), market.getSzAmount()
                    , market.getCyExchange(), market.getCyAmount()
                    , market.getBjExchange(), market.getBjAmount());

            OrmContext.getAccessor().insert(m);
        }

        log.info("atSaveMarketAsk markets:[{}]", markets.size());
        NetContext.getRouter().send(session, new SaveMarketAnswer(StringUtils.format("成功保存总数[{}]", markets.size())));
    }

    @PacketReceiver
    public void atMarketRequest(Session session, MarketRequest request) {
        var markets = marketCache.get().stream().limit(request.getNum()).toList();
        NetContext.getRouter().send(session, new MarketResponse(markets));
    }

}
