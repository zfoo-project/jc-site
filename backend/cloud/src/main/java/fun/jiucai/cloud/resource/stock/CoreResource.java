package fun.jiucai.cloud.resource.stock;

import com.zfoo.storage.anno.Id;
import com.zfoo.storage.anno.Index;
import com.zfoo.storage.anno.Storage;
import lombok.Getter;

@Getter
@Storage
public class CoreResource {

    @Id
    private int id;

    @Index(unique = false)
    private int type;

    private String word;

    @Override
    public String toString() {
        return word;
    }

}
