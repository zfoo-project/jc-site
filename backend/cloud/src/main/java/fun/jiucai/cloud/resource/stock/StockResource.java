package fun.jiucai.cloud.resource.stock;

import com.zfoo.storage.anno.Id;
import com.zfoo.storage.anno.Storage;
import lombok.Getter;

@Getter
@Storage("stocks")
public class StockResource {

    @Id
    private int code;

    private String name;

}
