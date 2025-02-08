package fun.jiucai.cloud.stock.service;

import com.zfoo.event.model.AppStartEvent;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.SingleCache;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.anno.StorageAutowired;
import com.zfoo.storage.model.IStorage;
import fun.jiucai.cloud.model.news.OneNews;
import fun.jiucai.cloud.resource.stock.StockConceptResource;
import fun.jiucai.cloud.resource.stock.StockResource;
import fun.jiucai.cloud.stock.util.ThsUtils;
import fun.jiucai.common.entity.NewsEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author godotg
 */
@Component
public class StockService implements ApplicationListener<AppStartEvent> {

    private SingleCache<List<Pair<Integer, String>>> conceptCache;

    @StorageAutowired
    private IStorage<Integer, StockResource> stockStorage;
    @StorageAutowired
    private IStorage<Integer, StockConceptResource> stockConceptStorage;


    @Override
    public void onApplicationEvent(AppStartEvent event) {
        conceptCache = SingleCache.build(6 * TimeUtils.MILLIS_PER_HOUR, () -> ThsUtils.allConcepts());
    }

    public String toFullContent(OneNews news) {
        var builder = new StringBuilder();

        if (StringUtils.isNotEmpty(news.getTitle())) {
            builder.append(StringUtils.trim(news.getTitle())).append(FileUtils.LS);
        }

        if (StringUtils.isNotEmpty(news.getContent())) {
            builder.append(StringUtils.trim(news.getContent())).append(FileUtils.LS);
        }

        if (CollectionUtils.isNotEmpty(news.getStocks())) {
            var stockNameList = news.getStocks().stream().map(it -> it.getName()).collect(Collectors.toList());
            builder.append(StringUtils.joinWith(StringUtils.COMMA, stockNameList.toArray())).append(FileUtils.LS);
        }

        if (CollectionUtils.isNotEmpty(news.getSubjects())) {
            for (var subject : news.getSubjects()) {
                builder.append(StringUtils.trim(subject.getSubjectName())).append(FileUtils.LS);
            }
        }

        return builder.toString();
    }

    public List<StockResource> selectStocks(OneNews news) {
        var content = toFullContent(news);

        var stockList = new ArrayList<StockResource>();
        for (var stockResource : stockStorage.getAll()) {
            if (content.contains(stockResource.getName())) {
                stockList.add(stockResource);
            }
        }

        // 重仓扫描
        if (CollectionUtils.isEmpty(stockList)) {

        }

        return stockList;
    }

    public List<NewsEntity.Concept> selectConcepts(OneNews news, List<StockResource> stocks) {
        var conceptLimit = 5;

        var content = toFullContent(news);
        var selectConceptCodes = new HashSet<Integer>();

        // 从文字里选择概念
        for (var conceptPair : conceptCache.get()) {
            if (content.contains(conceptPair.getValue())) {
                selectConceptCodes.add(conceptPair.getKey());
            }
        }

        // 从股票中选择概念
        for (var stock : stocks) {
            var stockConcepts = stockConceptStorage.getIndexes(StockConceptResource::getStock, stock.getCode());
            if (CollectionUtils.isEmpty(stockConcepts)) {
                continue;
            }

            selectConceptCodes.addAll(stockConcepts.stream().map(it -> it.getConcept()).toList());
        }

        var list = new ArrayList<NewsEntity.Concept>();
        for (var conceptCode : selectConceptCodes) {
            var priceAndRise = ThsUtils.conceptPriceAndRise(conceptCode);
            list.add(new NewsEntity.Concept(conceptCode, priceAndRise.getRise()));
        }

        return list.stream()
                .sorted((a, b) -> Float.compare(b.getRise(), a.getRise()))
                .limit(conceptLimit)
                .toList();
    }

    public String conceptName(int conceptCode) {
        return conceptCache.get()
                .stream()
                .filter(it -> it.getKey() == conceptCode)
                .map(it -> it.getValue())
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }

}
