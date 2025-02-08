package fun.jiucai.cloud.model.common;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class StockPriceAndRise {

    private float price;
    private float rise;

    public static StockPriceAndRise valueOf(float price, float rise) {
        var stock = new StockPriceAndRise();
        stock.price = price;
        stock.rise = rise;
        return stock;
    }

}
