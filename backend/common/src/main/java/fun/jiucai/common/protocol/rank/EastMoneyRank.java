package fun.jiucai.common.protocol.rank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EastMoneyRank {

    private int code;
    private String name;
    private int rankChange;
    // 重点关注
    private boolean primary;

}
