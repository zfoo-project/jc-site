package fun.jiucai.common.protocol.rank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThsRank {

    private int code;
    private String name;
    // 热度值
    private int rate;
    private int rankChange;
    private String analyse;
    private boolean primary;

}
