package fun.jiucai.common.protocol.news;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsStock {
    private String name;
    private int code;
    private String price;
    private String rise;
}
