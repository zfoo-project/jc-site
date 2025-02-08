package fun.jiucai.cloud.resource;

import com.zfoo.storage.anno.Id;
import com.zfoo.storage.anno.Storage;
import lombok.Getter;

@Getter
@Storage
public class KeyWordIgnoreResource {

    @Id
    private String word;

}
