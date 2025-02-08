package fun.jiucai.cloud.stock.event;

import com.zfoo.event.model.IEvent;
import fun.jiucai.common.entity.ConceptEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author godotg
 */
@Data
@AllArgsConstructor
public class GnEvent implements IEvent {

    private ConceptEntity conceptEntity;

}
