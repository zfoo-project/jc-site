package fun.jiucai.cloud.util;

public abstract class SpiderUtils {

    public static String spiderPath() {
        var spiderPath = System.getenv("jiucai.spider.path");
        if (spiderPath == null) {
            spiderPath = "../spider/spider.js";
        }
        return spiderPath;
    }

    public static String spiderWithFramePath() {
        var spiderPath = System.getenv("jiucai.spider.path");
        if (spiderPath == null) {
            spiderPath = "../spider/spider-with-frame.mjs";
        }
        return spiderPath;
    }

}
