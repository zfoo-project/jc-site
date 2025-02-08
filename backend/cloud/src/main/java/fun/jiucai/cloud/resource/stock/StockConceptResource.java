package fun.jiucai.cloud.resource.stock;

import com.zfoo.storage.anno.Id;
import com.zfoo.storage.anno.Index;
import com.zfoo.storage.anno.Storage;
import lombok.Getter;

@Getter
@Storage
public class StockConceptResource {

    @Id
    private int id;

    private int concept;

    @Index
    private int stock;

}
