package fun.jiucai.cloud.stock;

import com.zfoo.event.anno.EventReceiver;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.scheduler.anno.Scheduler;
import fun.jiucai.cloud.broker.BrokerService;
import fun.jiucai.cloud.stock.service.NewsService;
import fun.jiucai.cloud.stock.event.NewsEvent;
import fun.jiucai.common.entity.NewsEntity;
import fun.jiucai.common.protocol.broker.BrokerRegisterAsk;
import fun.jiucai.common.protocol.broker.SeoAsk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author godotg
 */
@Slf4j
@Component
public class SeoController {


    @Value("${myspring.profiles.active}")
    private String profile;

    public LinkedBlockingQueue<NewsEntity> seoNews = new LinkedBlockingQueue<>(300);

    @Autowired
    private NewsService newsService;

    @Autowired
    private BrokerService brokerService;

    @EventReceiver
    public void onNewsEvent(NewsEvent event) {
        if ("dev".equals(profile)) {
            return;
        }
        var entity = event.getNewsEntity();
        if (CollectionUtils.size(entity.getStocks()) >= 5) {
            return;
        }

        seoNews.offer(entity);
    }

    @Scheduler(cron = "0 0/3 * * * ?")
    public void cronSeo() {
        if (seoNews.isEmpty()) {
            return;
        }
        var first = seoNews.peek();
        var news = newsService.convertToNews(first);
        brokerService.broker(BrokerRegisterAsk.HOME, new SeoAsk(news));
        seoNews.poll();
    }

}
