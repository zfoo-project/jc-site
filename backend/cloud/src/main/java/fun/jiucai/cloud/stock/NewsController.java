/*
 * Copyright (C) 2020 The zfoo Authors
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package fun.jiucai.cloud.stock;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.zfoo.event.anno.EventReceiver;
import com.zfoo.event.manager.EventBus;
import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.SingleCache;
import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.statistic.event.StatNewsSearchRequestEvent;
import fun.jiucai.cloud.stock.service.NewsService;
import fun.jiucai.cloud.stock.event.NewsEvent;
import fun.jiucai.common.entity.NewsEntity;
import fun.jiucai.cloud.statistic.event.StatNewsRequestEvent;
import fun.jiucai.common.protocol.news.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author godotg
 */
@Component
public class NewsController implements ApplicationListener<AppStartEvent> {

    public static final int NEWS_QUERY_LIMIT = 10;

    public static SingleCache<Long> maxNewsIdCache;

    @EntityCacheAutowired
    private IEntityCache<Long, NewsEntity> newsEntityCaches;

    @Autowired
    private NewsService newsService;


    @Override
    public void onApplicationEvent(AppStartEvent event) {
        maxNewsIdCache = SingleCache.build(10 * TimeUtils.MILLIS_PER_SECOND, () -> MongoIdUtils.getMaxIdFromMongoDefault(NewsEntity.class));
    }

    @EventReceiver
    public void onNewsEvent(NewsEvent event) {
        newsEntityCaches.load(event.getNewsEntity().id());
        maxNewsIdCache.set(event.getNewsEntity().getId());
    }

    private boolean loadAndFilterNews(long id, List<News> news, int level) {
        var entity = newsEntityCaches.load(id);

        if (entity == null) {
            return false;
        }

        if (entity.getLevel() > level) {
            return false;
        }

        news.add(newsService.convertToNews(entity));
        return true;
    }

    @PacketReceiver
    public void atNewsRequest(Session session, NewsRequest request) {
        EventBus.post(StatNewsRequestEvent.INSTANCE);

        var endId = request.getEndId();
        var level = request.getLevel();

        var maxId = maxNewsIdCache.get();
        var minId = maxId - NEWS_QUERY_LIMIT * 4096;
        var news = new ArrayList<News>();

        // endId小于0为初始化，
        if (endId < 0) {
            for (var i = maxId; i > minId; i--) {
                loadAndFilterNews(i, news, level);
                if (news.size() >= NEWS_QUERY_LIMIT) {
                    break;
                }
            }
        } else {
            // load more
            for (var i = maxId; i > endId; i--) {
                loadAndFilterNews(i, news, level);
                if (news.size() >= NEWS_QUERY_LIMIT) {
                    break;
                }
            }
        }

        NetContext.getRouter().send(session, new NewsResponse(maxId, news));
    }

    @PacketReceiver
    public void atNewsLoadMoreRequest(Session session, NewsLoadMoreRequest request) {
        EventBus.post(StatNewsRequestEvent.INSTANCE);

        var startId = request.getStartId();
        var level = request.getLevel();

        var maxId = maxNewsIdCache.get();
        var minId = maxId - NEWS_QUERY_LIMIT * 4096;
        var news = new ArrayList<News>();

        for (var i = startId - 1; i > minId; i--) {
            if (loadAndFilterNews(i, news, level)) {
                startId = i;
            }
            if (news.size() >= NEWS_QUERY_LIMIT) {
                break;
            }
        }

        NetContext.getRouter().send(session, new NewsLoadMoreResponse(startId, news));
    }


    @PacketReceiver
    public void atNewsOneRequest(Session session, NewsOneRequest request) {
        var id = request.getId();
        var newsEntity = newsEntityCaches.load(id);
        if (newsEntity == null) {
            NetContext.getRouter().send(session, Message.valueError("情报不存在"));
            return;
        }

        var news = newsService.convertToNews(newsEntity);

        var response = new NewsOneResponse(news);
        NetContext.getRouter().send(session, response);
    }

    @PacketReceiver
    public void atNewsSearchRequest(Session session, NewsSearchRequest request) {
        var uid = session.getUid();
        if (uid <= 0) {
            NetContext.getRouter().send(session, Message.valueError("请先登录"));
            return;
        }

        var query = StringUtils.trim(request.getQuery());
        if (StringUtils.isEmpty(query)) {
            NetContext.getRouter().send(session, Message.valueError("搜索关键字不能为空"));
            return;
        }
        EventBus.post(StatNewsSearchRequestEvent.INSTANCE);

        var collection = OrmContext.getOrmManager().getCollection(NewsEntity.class);
        var regex = StringUtils.format("^.*{}.*", query);
        var news = new ArrayList<News>();
        collection.find(Filters.regex("content", regex))
                .sort(Sorts.descending("_id"))
                .limit(300)
                .forEach(it -> news.add(newsService.convertToNewsYear(it)));

        NetContext.getRouter().send(session, new NewsSearchResponse(news));
    }


}
