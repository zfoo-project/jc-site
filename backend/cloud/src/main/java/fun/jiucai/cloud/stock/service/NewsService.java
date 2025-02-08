package fun.jiucai.cloud.stock.service;

import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.anno.StorageAutowired;
import com.zfoo.storage.model.IStorage;
import fun.jiucai.cloud.resource.stock.StockResource;
import fun.jiucai.cloud.stock.event.NewsLevelEnum;
import fun.jiucai.cloud.util.DateUtils;
import fun.jiucai.cloud.stock.util.StockUtils;
import fun.jiucai.common.entity.NewsEntity;
import fun.jiucai.common.protocol.news.News;
import fun.jiucai.common.protocol.news.NewsConcept;
import fun.jiucai.common.protocol.news.NewsStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author godotg
 */
@Component
public class NewsService {

    @StorageAutowired
    private IStorage<Integer, StockResource> stockStorage;
    @Autowired
    private StockService stockService;

    public News convertToNews(NewsEntity entity) {
        var dateStr = DateUtils.dateFormatForDayTimeString(entity.getCtime());
        var stocks = new ArrayList<NewsStock>();
        if (CollectionUtils.isNotEmpty(entity.getStocks())) {
            for (var stock : entity.getStocks()) {
                StockResource stockResource = null;
                try {
                    stockResource = stockStorage.get(stock.getCode());
                } catch (Exception e) {
                }
                if (stockResource == null) {
                    continue;
                }
                var price = StockUtils.toSimpleRatio(stock.getPrice());
                var rise = StockUtils.toSimpleRatio(stock.getRise());
                stocks.add(new NewsStock(stockResource.getName(), stock.getCode(), price, rise));
            }
        }

        var concepts = new ArrayList<NewsConcept>();
        if (CollectionUtils.isNotEmpty(entity.getConcepts())) {
            for (var concept : entity.getConcepts()) {
                var conceptName = stockService.conceptName(concept.getCode());
                if (StringUtils.isEmpty(conceptName)) {
                    continue;
                }
                var rise = StockUtils.toSimpleRatio(concept.getRise());
                concepts.add(new NewsConcept(conceptName, concept.getCode(), rise));
            }
        }


        var news = new News();
        news.setId(entity.getId());
        news.setLevel(NewsLevelEnum.newsLevelOfType(entity.getLevel()).name());
        news.setTitle(entity.getTitle());
        news.setContent(entity.getContent());
        news.setCtime(dateStr);
        news.setStocks(stocks);
        news.setConcepts(concepts);
        news.setSubjects(entity.getSubjects());

        return news;
    }

    public News convertToNewsYear(NewsEntity entity) {
        var news = convertToNews(entity);
        var dateStr = TimeUtils.dateFormatForDayTimeString(entity.getCtime());
        news.setCtime(dateStr);
        return news;
    }


}
