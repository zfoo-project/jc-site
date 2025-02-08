package fun.jiucai.cloud.stock.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import fun.jiucai.common.protocol.rank.EastMoneyRank;
import fun.jiucai.common.util.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class EastMoneyUtils {


    public static List<EastMoneyConcept> allConcepts() {
        var url = "https://53.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=1000&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:90+t:3+f:!50";
        var responseBody = HttpUtils.getNoHeaders(url);

        var response = JsonUtils.string2Object(responseBody, EastMoneyResult.class);
        var data = response.getData();
        if (data == null) {
            return Collections.emptyList();
        }

        var diff = data.getDiff();
        if (CollectionUtils.isEmpty(diff)) {
            return Collections.emptyList();
        }

        return diff;
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EastMoneyData {
        private int total;
        private List<EastMoneyConcept> diff;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EastMoneyConcept {
        // BK1077
        private String f12;
        // 90
        private String f13;
        @JsonProperty("f14")
        private String concept;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EastMoneyResult {
        private int lt;
        private EastMoneyData data;
    }


    // -----------------------------------------------------------------------------------------------------------------
    public static List<EastMoneyRank> rank100() {
        var url = "https://emappdata.eastmoney.com/stockrank/getAllCurrentList";
        var responseBody = HttpUtils.post(url, new RankRequest("appId01", "786e4c21-70dc-435a-93bb-38", "", 1, 100));
        var response = JsonUtils.string2Object(responseBody, RankResponse.class);
        var list = new ArrayList<EastMoneyRank>();
        for (var stock : response.data) {
            var code = StockUtils.formatPrefixCode(stock.code);
            var name = StockUtils.toStockName(code);
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            list.add(new EastMoneyRank(code, name, stock.rankChange, false));
        }
        return list;
    }

    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RankRequest {
        private String appId;
        private String globalId;
        private String marketType;
        private int pageNo;
        private int pageSize;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RankResponse {
        private String message;
        private List<RankResult> data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RankResult {
        // SH601727
        @JsonProperty("sc")
        private String code;
        @JsonProperty("hisRc")
        private int rankChange;
    }

}
