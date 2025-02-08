package fun.jiucai.common.protocol.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    private long time;
    // 不同ip的个数
    private int ips;
    // 活跃连接总数
    private int active;
    // 请求次数
    private int newsRequest;
    private int newsSearchRequest;
    private int chatgptRequest;
    private int googleSearch;
    private int bingSearch;
    private int weixinSearch;
    private int bilibiliSearch;
    private int midImagineRequest;
    private int sdSimulateRequest;
    private int animationRequest;
    private int navigation;
    private NewsStat newsStat;

}
