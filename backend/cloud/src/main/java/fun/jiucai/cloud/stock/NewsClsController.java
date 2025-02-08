package fun.jiucai.cloud.stock;


import com.zfoo.event.manager.EventBus;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.anno.StorageAutowired;
import com.zfoo.storage.model.IStorage;
import fun.jiucai.cloud.model.common.StockPriceAndRise;
import fun.jiucai.cloud.model.news.OneNews;
import fun.jiucai.cloud.model.news.Telegraph;
import fun.jiucai.cloud.resource.*;
import fun.jiucai.cloud.resource.stock.StockResource;
import fun.jiucai.cloud.stock.service.StockService;
import fun.jiucai.cloud.stock.event.NewsLevelEnum;
import fun.jiucai.cloud.stock.event.NewsEvent;
import fun.jiucai.cloud.util.CopyrightUtils;
import fun.jiucai.cloud.stock.util.StockUtils;
import fun.jiucai.common.entity.HistoryEntity;
import fun.jiucai.common.entity.NewsEntity;
import fun.jiucai.common.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NewsClsController {

    @Autowired
    private StockService stockService;

    @StorageAutowired
    private IStorage<String, KeyWordResource> keyWordStorage;
    @StorageAutowired
    private IStorage<String, KeyWordIgnoreResource> keyWordIgnoreStorage;
    @StorageAutowired
    private IStorage<String, PushWordResource> pushWordStorage;

    @EntityCacheAutowired
    private IEntityCache<String, HistoryEntity> historyEntityCaches;

    @Value("${jiucai.spider.path}")
    private String spiderPath;

    @Scheduler(cron = "3 0/4 8-17 ? * MON-FRI")
    public void cronClsSpider0() {
        cronClsSpider();
    }

    @Scheduler(cron = "30 0/30 * * * ?")
    public void cronClsSpider2() {
        cronClsSpider();
    }

    @Scheduler(cron = "0 0 9 * * ?")
    public void cronClsSpider00() {
        cronClsSpider();
    }

    @Scheduler(cron = "0 15 9 * * ?")
    public void cronClsSpider01() {
        cronClsSpider();
    }

    @Scheduler(cron = "0 20 9 * * ?")
    public void cronClsSpider02() {
        cronClsSpider();
    }

    @Scheduler(cron = "0 22 9 * * ?")
    public void cronClsSpider1() {
        cronClsSpider();
    }

    @Scheduler(cron = "0 23 9 * * ?")
    public void cronClsSpide2() {
        cronClsSpider();
    }

    @Scheduler(cron = "0 25 9 * * ?")
    public void cronClsSpider3() {
        cronClsSpider();
    }

    @Scheduler(cron = "0 10 20 * * ?")
    public void cronClsSpider4() {
        cronClsSpider();
    }

    @Scheduler(cron = "0 40 20 * * ?")
    public void cronClsSpider5() {
        cronClsSpider();
    }

    public void cronClsSpider() {
        var response = requestForTelegraph();
        var telegraphNews = toNews(response);
        if (CollectionUtils.isEmpty(telegraphNews)) {
            return;
        }
        telegraphNews.sort((a, b) -> Long.compare(a.getCtime(), b.getCtime()));
        var historyEntity = historyEntityCaches.load(HistoryEntity.CLS_HISTORY_ID);
        for (var news : telegraphNews) {
            var newsId = news.getId();
            if (historyEntity.getHistory().contains(newsId)) {
                continue;
            }

            if (!updateNews(news)) {
                continue;
            }

            historyEntity.addHistory1000(newsId);
            historyEntityCaches.update(historyEntity);
        }
    }

    public boolean updateNews(OneNews news) {
        var stockList = stockService.selectStocks(news);

        var level = StringUtils.trim(news.getLevel());
        var title = StringUtils.trim(news.getTitle());
        var content = StringUtils.trim(news.getContent());
        var ctime = news.getCtime() * TimeUtils.MILLIS_PER_SECOND;

        var hasKeyWords = keyWordStorage.getAll().stream().map(it -> it.getWord()).anyMatch(it -> content.contains(it));

        // 过滤不需要的新闻，有图片的需要过滤
        if (StringUtils.isNotEmpty(news.getImg())) {
            return false;
        }
        if (keyWordIgnoreStorage.getAll().stream().anyMatch(it -> content.contains(it.getWord()))) {
            return false;
        }

        if (level.equals("A")) {
            level = NewsLevelEnum.S.name();
        } else if (level.equals("B")) {
            if (hasKeyWords) {
                level = NewsLevelEnum.A.name();
            } else {
                level = NewsLevelEnum.B.name();
            }
        } else if (hasKeyWords) {
            if (CollectionUtils.isEmpty(stockList)) {
                level = NewsLevelEnum.C.name();
            } else {
                level = NewsLevelEnum.D.name();
            }
        } else {
            return false;
        }

        if (StringUtils.isNotEmpty(news.getImg())) {
            return false;
        }

        // 添加相关股票--------------------------------------------------------------------------------------------
        var stocks = new ArrayList<NewsEntity.Stock>();
        if (CollectionUtils.isNotEmpty(stockList)) {
            var stockMap = new HashMap<StockResource, StockPriceAndRise>();
            for (var stock : stockList) {
                var fiveRange = StockUtils.stockPriceAndRise(stock.getCode());
                stockMap.put(stock, fiveRange);
            }
            if (CollectionUtils.isNotEmpty(stockMap)) {
                stockMap.entrySet()
                        .stream()
                        .sorted((a, b) -> Float.compare(b.getValue().getRise(), a.getValue().getRise()))
                        .limit(5)
                        .forEach(it -> {
                            var stock = it.getKey();
                            var stockPriceAndRise = it.getValue();
                            stocks.add(new NewsEntity.Stock(stock.getCode(), stockPriceAndRise.getPrice(), stockPriceAndRise.getRise()));
                        });
            }
        }

        // 添加板块
        var concepts = stockService.selectConcepts(news, stockList);

        // 添加关键词
        var subjects = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(news.getSubjects())) {
            news.getSubjects()
                    .stream()
                    .limit(5)
                    .forEach(it -> subjects.add(it.getSubjectName()));
        }

        var newsEntity = new NewsEntity();
        newsEntity.setId(MongoIdUtils.getIncrementIdFromMongoDefault(NewsEntity.class));
        newsEntity.setLevel(NewsLevelEnum.newsLevelOfName(level).getType());
        newsEntity.setTitle(CopyrightUtils.noCopyright(title));
        newsEntity.setContent(CopyrightUtils.noCopyright(content));
        newsEntity.setCtime(ctime);
        newsEntity.setStocks(stocks);
        newsEntity.setConcepts(concepts);
        newsEntity.setSubjects(subjects);
        OrmContext.getAccessor().insert(newsEntity);
        EventBus.post(new NewsEvent(newsEntity));
        log.info("telegraph -> [{}] [{}] [{}]", newsEntity.getId(), level, newsEntity.getTitle());
        return true;
    }

    /**
     * 获取最新电报
     */
    public Telegraph requestForTelegraph() {
        var url = "https://www.cls.cn/nodeapi/updateTelegraphList";
        var responseBody = HttpUtils.puppeteer(url, spiderPath);
        var document = Jsoup.parse(responseBody);
        responseBody = document.getElementsByTag("pre").text();
        var response = JsonUtils.string2Object(responseBody, Telegraph.class);
        return response;
    }

    private List<OneNews> toNews(Telegraph telegraph) {
        if (telegraph.getData() == null) {
            return Collections.emptyList();
        }

        var rollData = telegraph.getData().getRollData();
        if (CollectionUtils.isEmpty(rollData)) {
            return Collections.emptyList();
        }

        var telegraphNews = rollData.stream().filter(it -> it.getType() == -1).collect(Collectors.toList());
        return telegraphNews;
    }

}
