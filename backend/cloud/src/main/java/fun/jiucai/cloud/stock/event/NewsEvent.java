package fun.jiucai.cloud.stock.event;

import com.zfoo.event.model.IEvent;
import fun.jiucai.common.entity.NewsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author godotg
 */
@Data
@AllArgsConstructor
public class NewsEvent implements IEvent {

    private NewsEntity newsEntity;

}
