package fun.jiucai.cloud.resource;

import com.zfoo.storage.anno.Id;
import com.zfoo.storage.anno.Storage;
import lombok.Getter;

@Getter
@Storage
public class KeyWordResource {

    @Id
    private String word;

}
