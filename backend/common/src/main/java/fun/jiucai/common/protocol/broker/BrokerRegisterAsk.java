package fun.jiucai.common.protocol.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerRegisterAsk {

    // 不带gpu的服务器
    public static final int HOME = 1;
    // 带了gpu的服务器
    public static final int HOME_GPU = 2;

    private int brokerType;

}
