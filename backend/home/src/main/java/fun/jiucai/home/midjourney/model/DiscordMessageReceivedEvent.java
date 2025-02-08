package fun.jiucai.home.midjourney.model;

import com.zfoo.event.model.IEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordMessageReceivedEvent implements IEvent {

    private MessageReceivedEvent messageReceivedEvent;

}
