package fun.jiucai.home.manager;

import com.zfoo.event.model.AppStartEvent;
import com.zfoo.orm.OrmContext;
import fun.jiucai.common.entity.KeyValueEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author sun
 */
@Component
public class BiosManager implements ApplicationListener<AppStartEvent>, Ordered {

    @Override
    public void onApplicationEvent(AppStartEvent event) {
        var v2rayConfigIdEntity = OrmContext.getAccessor().load(KeyValueEntity.v2ray_config_id, KeyValueEntity.class);
        if (v2rayConfigIdEntity == null) {
            v2rayConfigIdEntity = new KeyValueEntity();
            v2rayConfigIdEntity.setId(KeyValueEntity.v2ray_config_id);
            OrmContext.getAccessor().insert(v2rayConfigIdEntity);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
