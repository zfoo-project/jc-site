package fun.jiucai.cloud.stock.util;

import com.zfoo.protocol.exception.RunException;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.cloud.model.common.StockPriceAndRise;
import fun.jiucai.cloud.util.SpiderUtils;
import fun.jiucai.common.util.HttpUtils;
import io.netty.util.collection.IntObjectHashMap;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Map;

/**
 * @author godotg
 */
public abstract class StockUtils {

    private static final Logger logger = LoggerFactory.getLogger(StockUtils.class);

    private static final IntObjectHashMap<String> stockMap = new IntObjectHashMap<>();

    static {
        var stocks = ClassUtils.getFileFromClassPathToString("excel/stock/stocks.csv");
        var splits = stocks.split(FileUtils.LS_REGEX);
        for (int i = 1; i < splits.length; i++) {
            var row = splits[i];
            var cols = row.split(StringUtils.COMMA_REGEX);
            stockMap.put(Integer.parseInt(cols[0]), cols[1]);
        }
    }

    public static String toStockName(int code) {
        return stockMap.containsKey(code) ? stockMap.get(code) : StringUtils.EMPTY;
    }

    public static String formatCode(int code) {
        var stockCode = String.valueOf(code);
        switch (stockCode.length()) {
            case 0:
                stockCode = "000000";
                break;
            case 1:
                stockCode = StringUtils.format("00000{}", stockCode);
                break;
            case 2:
                stockCode = StringUtils.format("0000{}", stockCode);
                break;
            case 3:
                stockCode = StringUtils.format("000{}", stockCode);
                break;
            case 4:
                stockCode = StringUtils.format("00{}", stockCode);
                break;
            case 5:
                stockCode = StringUtils.format("0{}", stockCode);
                break;
            case 6:
                break;
            default:
        }
        return stockCode;
    }

    public static int formatPrefixCode(String code) {
        code = StringUtils.trim(code).toLowerCase();
        code = code.replace("sz", StringUtils.EMPTY);
        code = code.replace("sh", StringUtils.EMPTY);
        return Integer.parseInt(code);
    }

    public static String hsCode(int code) {
        var stockCode = formatCode(code);
        stockCode = stockCode.startsWith("6") ? StringUtils.format("sh{}", stockCode) : StringUtils.format("sz{}", stockCode);
        return stockCode;
    }


    /**
     * 是否在交易时间
     */
    public static boolean tradingTime() {
        try {
            var now = TimeUtils.now();
            var simpleDateStr = TimeUtils.dateFormatForDayString(now);
            var startTime = TimeUtils.stringToDate(StringUtils.format("{} 09:10:00", simpleDateStr)).getTime();
            var endTime = TimeUtils.stringToDate(StringUtils.format("{} 11:30:00", simpleDateStr)).getTime();

            if (TimeUtils.timeBetween(now, startTime, endTime)) {
                return true;
            }

            startTime = TimeUtils.stringToDate(StringUtils.format("{} 12:50:00", simpleDateStr)).getTime();
            endTime = TimeUtils.stringToDate(StringUtils.format("{} 15:00:00", simpleDateStr)).getTime();

            if (TimeUtils.timeBetween(now, startTime, endTime)) {
                return true;
            }

            return false;
        } catch (ParseException e) {
            logger.error("开盘时间解析错误");
        }

        return false;
    }

    public static String toSimpleRatio(float value) {
        var decimal = new BigDecimal(value);
        return decimal.setScale(1, RoundingMode.HALF_UP).toString();
    }


    // **************************************************股票相关********************************************************


    /**
     * 通过爬虫获取股票价格有概率失败，所以一共有3个实现，轮流使用不同的实现
     *
     * @param code 股票的代码
     */
    public static final float DEFAULT_VAlUE = 88.8F;

    public static StockPriceAndRise stockPriceAndRise(int code) {
        try {
            return doGetBySina(code);
        } catch (Exception e) {
            logger.error("新浪api获取股票数据异常");
        }

        try {
            return doGetByQQ(code);
        } catch (Exception e) {
            logger.error("腾讯api获取股票数据异常");
        }

        try {
            return doGetByThs(code);
        } catch (Exception e) {
            logger.error("同花顺接口api获取股票数据异常");
        }

        logger.error("获取股票数据异常，没有任何一个接口可以获取到股票数据");
        return StockPriceAndRise.valueOf(DEFAULT_VAlUE, DEFAULT_VAlUE);
    }

    // -----------------------------------------------------------------------------------------------------------------

    public static StockPriceAndRise doGetBySina(int code) {
        try {
            var stockCode = StockUtils.formatCode(code);
            if (stockCode.startsWith("0") || stockCode.startsWith("3")) {
                stockCode = "s_sz" + stockCode;
            } else if (stockCode.startsWith("6") || stockCode.startsWith("9")) {
                stockCode = "s_sh" + stockCode;
            } else if (stockCode.startsWith("4") || stockCode.startsWith("8")) {
                stockCode = "s_bj" + stockCode;
            }

            var url = StringUtils.format("https://hq.sinajs.cn/list={}", stockCode);
            var responseBody = HttpUtils.getWithHeaders(url, Map.of(
                    "Host", "hq.sinajs.cn",
                    "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/110.0",
                    "Accept", "*/*",
                    "Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2",
                    "Accept-Encoding", "gzip, deflate, br",
                    "Referer", "http://vip.stock.finance.sina.com.cn/",
                    "Connection", "keep-alive",
                    "Sec-Fetch-Dest", "script",
                    "Sec-Fetch-Mode", "no-cors",
                    "Sec-Fetch-Site", "cross-site"
            ));
            // var hq_str_s_sh603182="嘉华股份,12.050,0.680,5.98,55412,6565";
            var result = StringUtils.substringAfterFirst(responseBody, "=\"");
            var splits = result.split(StringUtils.COMMA_REGEX);
            var stockPriceAndRise = StockPriceAndRise.valueOf(Float.parseFloat(splits[1]), Float.parseFloat(splits[3]));
            return stockPriceAndRise;
        } catch (Exception e) {
            throw new RunException(e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static StockPriceAndRise doGetByQQ(int code) {
        try {
            var stockCode = StockUtils.formatCode(code);
            if (stockCode.startsWith("0") || stockCode.startsWith("3")) {
                stockCode = "sz" + stockCode;
            } else if (stockCode.startsWith("6") || stockCode.startsWith("9")) {
                stockCode = "sh" + stockCode;
            } else if (stockCode.startsWith("4") || stockCode.startsWith("8")) {
                stockCode = "bj" + stockCode;
            }

            var url = StringUtils.format("https://web.sqt.gtimg.cn/q={}", stockCode);
            var responseBody = HttpUtils.getNoHeaders(url);
            // var hq_str_s_sh603182="嘉华股份,12.050,0.680,5.98,55412,6565";
            var splits = responseBody.split("~");
            var stockPriceAndRise = StockPriceAndRise.valueOf(Float.parseFloat(splits[3]), Float.parseFloat(splits[32]));
            return stockPriceAndRise;
        } catch (Exception e) {
            throw new RunException(e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static StockPriceAndRise doGetByThs(int code) {
        var stockCode = StockUtils.formatCode(code);
        var url = StringUtils.format("https://stockpage.10jqka.com.cn/{}/", stockCode);
        var responseBody = HttpUtils.puppeteer(url, SpiderUtils.spiderWithFramePath());
        var document = Jsoup.parse(responseBody);
        var price = document.getElementById("hexm_curPrice").text();
        var rate = document.getElementById("hexm_float_rate").text();
        rate = StringUtils.substringBeforeFirst(rate, "%");
        return StockPriceAndRise.valueOf(Float.parseFloat(price), Float.parseFloat(rate));
    }


}
