package fun.jiucai.cloud.stock;

import com.zfoo.event.model.AppStartEvent;
import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.scheduler.util.SingleCache;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.anno.StorageAutowired;
import com.zfoo.storage.model.IStorage;
import fun.jiucai.cloud.resource.stock.CoreResource;
import fun.jiucai.cloud.stock.event.NewsLevelEnum;
import fun.jiucai.cloud.util.DateUtils;
import fun.jiucai.common.entity.ConceptEntity;
import fun.jiucai.common.protocol.concept.Concept;
import fun.jiucai.common.protocol.concept.ConceptRequest;
import fun.jiucai.common.protocol.concept.ConceptResponse;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author godotg
 */
@Component
public class ConceptController implements ApplicationListener<AppStartEvent> {

    @StorageAutowired
    private IStorage<Integer, CoreResource> coreStorage;

    private SingleCache<List<Concept>> conceptCache;

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        conceptCache = SingleCache.build(60 * TimeUtils.MILLIS_PER_SECOND, () -> {
            var concepts = OrmContext.getQuery(ConceptEntity.class).queryAll().stream()
                    .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                    .limit(180)
                    .collect(Collectors.toList());

            var list = new ArrayList<Concept>();
            for (var ele : concepts) {
                var dateStr = DateUtils.dateFormatForDayTimeString(ele.getCtime());

                var concept = new Concept();
                concept.setId(ele.getId());
                concept.setLevel(NewsLevelEnum.S.name());
                concept.setTitle(ele.getTitle());
                concept.setContent(ele.getContent());
                concept.setUrl(ele.getUrl());
                concept.setCtime(dateStr);

                list.add(concept);
            }
            return list;
        });
    }

    @PacketReceiver
    public void atConceptRequest(Session session, ConceptRequest request) {
        var coreResource = RandomUtils.randomEle(coreStorage.getIndexes(CoreResource::getType, 1));
        var gnList = conceptCache.get().stream().limit(request.getNum()).toList();
        NetContext.getRouter().send(session, new ConceptResponse(gnList, coreResource.getWord()));
    }

}
