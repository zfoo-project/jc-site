package fun.jiucai.home.midjourney.model;

import com.zfoo.event.model.IEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordMessageUpdateEvent implements IEvent {

    private MessageUpdateEvent messageUpdateEvent;

}
