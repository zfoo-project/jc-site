package fun.jiucai.cloud.controller;

import com.mongodb.client.model.Sorts;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.anno.Task;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.session.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.util.RandomUtils;
import fun.jiucai.cloud.model.CodeEnum;
import fun.jiucai.cloud.statistic.StatisticsService;
import fun.jiucai.cloud.stock.service.BroadcastService;
import fun.jiucai.common.entity.BroadcastEntity;
import fun.jiucai.common.entity.StatisticsEntity;
import fun.jiucai.common.protocol.admin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author godotg
 */
@Component
public class AdminController {

    @Autowired
    private BroadcastService broadcastService;
    @Autowired
    private StatisticsService statisticsService;

    @PacketReceiver
    public void atAdminInfoRequest(Session session, AdminInfoRequest request) {
        statisticsService.refreshStatistics();

        var broadcasts = broadcastService.allBroadcast();
        var collection = OrmContext.getOrmManager().getCollection(StatisticsEntity.class);
        var list = new ArrayList<Statistics>();
        collection.find()
                .sort(Sorts.descending("_id"))
                .limit(180)
                .forEach(it -> list.add(it.toSystem()));

        var response = new AdminInfoResponse(broadcasts, list);
        NetContext.getRouter().send(session, response);
    }

    @PacketReceiver
    public void atDeleteBroadcastRequest(Session session, DeleteBroadcastRequest request) {
        var id = request.getId();
        OrmContext.getAccessor().delete(id, BroadcastEntity.class);
        NetContext.getRouter().send(session, new DeleteBroadcastResponse(broadcastService.allBroadcast()));
    }

    @PacketReceiver(Task.EventBus)
    public void atDoBroadcastRequest(Session session, DoBroadcastRequest request) throws Exception {
        var id = request.getId();
        var type = request.getType();
        var entity = OrmContext.getAccessor().load(id, BroadcastEntity.class);
        if (entity == null) {
            NetContext.getRouter().send(session, Error.valueOf(CodeEnum.PARAMETER_ERROR.getMessage()));
            return;
        }
        broadcastService.doBroadcast(id, type);
        NetContext.getRouter().send(session, new DoBroadcastResponse(broadcastService.allBroadcast()));
    }

}
