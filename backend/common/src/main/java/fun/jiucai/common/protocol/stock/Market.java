package fun.jiucai.common.protocol.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Market {

    private long date;

    // 股票数量
    private int stockNum;

    // 涨幅大于0的股票数
    private int stockNum0;

    // 涨幅大于-0.5的股票数
    private int stockNumNeg005;

    // 涨幅大于-1的股票数
    private int stockNumNeg10;

    // 总价
    private long totalPrice;

    // 自定义指数，主要是去除银行的总流通市值
    private long marketIndex;
    private long shMarketIndex;
    private long kcMarketIndex;
    private long szMarketIndex;
    private long cyMarketIndex;
    private long bjMarketIndex;

    // 成交量
    private long exchange;

    // 总的流通市值
    private long amount;

    private long shExchange;
    private long shAmount;

    private long kcExchange;
    private long kcAmount;

    private long szExchange;
    private long szAmount;

    private long cyExchange;
    private long cyAmount;

    private long bjExchange;
    private long bjAmount;

}
