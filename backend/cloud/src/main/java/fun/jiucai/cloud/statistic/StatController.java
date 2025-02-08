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

package fun.jiucai.cloud.statistic;

import com.zfoo.event.anno.EventReceiver;
import com.zfoo.event.model.AppStartAfterEvent;
import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.core.event.ServerSessionActiveEvent;
import com.zfoo.net.packet.common.Ping;
import com.zfoo.net.packet.common.Pong;
import com.zfoo.net.session.Session;
import com.zfoo.net.util.SessionUtils;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.anno.EntityCacheAutowired;
import com.zfoo.orm.cache.IEntityCache;
import com.zfoo.scheduler.anno.Scheduler;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.model.event.ChatgptEvent;
import fun.jiucai.cloud.statistic.event.*;
import fun.jiucai.cloud.stock.event.NewsEvent;
import fun.jiucai.cloud.stock.event.NewsLevelEnum;
import fun.jiucai.common.entity.HistoryEntity;
import fun.jiucai.common.entity.StatisticsEntity;
import fun.jiucai.common.protocol.admin.StatisticsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author godotg
 */
@Slf4j
@Component
public class StatController implements ApplicationListener<ApplicationContextEvent>, Ordered {

    @EntityCacheAutowired
    private IEntityCache<String, HistoryEntity> historyEntityCaches;
    @EntityCacheAutowired
    private IEntityCache<Long, StatisticsEntity> statisticsEntityCaches;
    @Autowired
    private StatisticsService statisticsService;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof AppStartEvent) {
            var clsEntity = OrmContext.getAccessor().load(HistoryEntity.CLS_HISTORY_ID, HistoryEntity.class);
            if (clsEntity == null) {
                clsEntity = new HistoryEntity();
                clsEntity.setId(HistoryEntity.CLS_HISTORY_ID);
                OrmContext.getAccessor().insert(clsEntity);
            }

            var ipEntity = OrmContext.getAccessor().load(HistoryEntity.IP_HISTORY_ID, HistoryEntity.class);
            if (ipEntity == null) {
                ipEntity = new HistoryEntity();
                ipEntity.setId(HistoryEntity.IP_HISTORY_ID);
                OrmContext.getAccessor().insert(ipEntity);
            }
        } else if (event instanceof AppStartAfterEvent) {
        } else if (event instanceof ContextClosedEvent) {
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    // ---------------------------------------------------------------------------------------------------
    @PacketReceiver
    public void atPing(Session session, Ping ping) {
        NetContext.getRouter().send(session, Pong.valueOf(TimeUtils.now()));
    }


    // 保存总ip
    @Scheduler(cron = "30 58 23 * * ?")
    public void cronStat() {
        statisticsService.refreshStatistics();
        // 清除ip的记录缓存
        var historyEntity = historyEntityCaches.load(HistoryEntity.IP_HISTORY_ID);
        historyEntity.setHistory(new ArrayList<>());
        historyEntityCaches.update(historyEntity);
    }

    @EventReceiver
    public void onServerSessionActiveEvent(ServerSessionActiveEvent event) {
        var statEntity = statisticsService.loadCurrentStatEntity();
        statEntity.setActive(statEntity.getActive() + 1);
        statisticsEntityCaches.updateUnsafe(statEntity);

        SchedulerBus.schedule(() -> {
            var ipLong = SessionUtils.toIpLong(event.getSession());
            var historyEntity = historyEntityCaches.load(HistoryEntity.IP_HISTORY_ID);
            historyEntity.addHistoryUnique5000(ipLong);
            historyEntityCaches.updateUnsafe(historyEntity);
        }, 0, TimeUnit.MILLISECONDS);
    }

    @EventReceiver
    public void onStatNewsRequestEvent(StatNewsRequestEvent event) {
        var statEntity = statisticsService.loadCurrentStatEntity();
        statEntity.setNewsRequest(statEntity.getNewsRequest() + 1);
        statisticsEntityCaches.updateUnsafe(statEntity);
    }

    @EventReceiver
    public void onStatNewsSearchRequestEvent(StatNewsSearchRequestEvent event) {
        var statEntity = statisticsService.loadCurrentStatEntity();
        statEntity.setNewsSearchRequest(statEntity.getNewsSearchRequest() + 1);
        statisticsEntityCaches.updateUnsafe(statEntity);
    }

    @EventReceiver
    public void onChatgptEvent(ChatgptEvent event) {
        var statEntity = statisticsService.loadCurrentStatEntity();
        statEntity.setChatgptRequest(statEntity.getChatgptRequest() + 1);
        if (event.isGoogleSearch()) {
            statEntity.setGoogleSearch(statEntity.getGoogleSearch() + 1);
        }
        if (event.isBingSearch()) {
            statEntity.setBingSearch(statEntity.getBingSearch() + 1);
        }
        if (event.isWeixinSearch()) {
            statEntity.setWeixinSearch(statEntity.getWeixinSearch() + 1);
        }
        if (event.isBilibiliSearch()) {
            statEntity.setBilibiliSearch(statEntity.getBilibiliSearch() + 1);
        }
        statisticsEntityCaches.updateUnsafe(statEntity);
    }

    @EventReceiver
    public void onStatMidImagineRequestEvent(StatMidImagineRequestEvent event) {
        var statEntity = statisticsService.loadCurrentStatEntity();
        statEntity.setMidImagineRequest(statEntity.getMidImagineRequest() + 1);
        statisticsEntityCaches.updateUnsafe(statEntity);
    }

    @EventReceiver
    public void onStatAnimationRequestEvent(StatAnimationRequestEvent event) {
        var statEntity = statisticsService.loadCurrentStatEntity();
        statEntity.setAnimationRequest(statEntity.getAnimationRequest() + 1);
        statisticsEntityCaches.updateUnsafe(statEntity);
    }

    @EventReceiver
    public void onStatSdSimulateRequestEvent(StatSdSimulateRequestEvent event) {
        var statEntity = statisticsService.loadCurrentStatEntity();
        statEntity.setSdSimulateRequest(statEntity.getSdSimulateRequest() + 1);
        statisticsEntityCaches.updateUnsafe(statEntity);
    }

    @PacketReceiver
    public void atStatisticsRequest(Session session, StatisticsRequest request) {
        var statEntity = statisticsService.loadCurrentStatEntity();
        statEntity.setNavigation(statEntity.getNavigation() + 1);
        statisticsEntityCaches.updateUnsafe(statEntity);
    }

    @EventReceiver
    public void onNewsEvent(NewsEvent event) {
        var newsLevel = NewsLevelEnum.newsLevelOfType(event.getNewsEntity().getLevel());
        var statEntity = statisticsService.loadCurrentStatEntity();
        var newsStat = statEntity.getNewsStat();
        switch (newsLevel) {
            case S -> newsStat.setNewsS(newsStat.getNewsS() + 1);
            case A -> newsStat.setNewsA(newsStat.getNewsA() + 1);
            case B -> newsStat.setNewsB(newsStat.getNewsB() + 1);
            case C -> newsStat.setNewsC(newsStat.getNewsC() + 1);
            case D -> newsStat.setNewsD(newsStat.getNewsD() + 1);
        }
        statisticsEntityCaches.updateUnsafe(statEntity);
    }

}
