package fun.jiucai.common.protocol.rank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankResponse {

    private List<EastMoneyRank> eastMoneyRanks;

    private List<ThsRank> thsRanks;

    private String core;

}
