/*
 * Copyright (C) 2020 The zfoo Authors
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package fun.jiucai.cloud.util.xdb;

import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.protocol.util.RandomUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 */
public class XdbUtils {

    private static final List<String> regions = List.of("火星", "月亮", "天马座", "地球", "星星");
    private static final byte[] cBuff;

    static {
        try {
            var inputStream = ClassUtils.getFileFromClassPath("ip2region.xdb");
            cBuff = Searcher.loadContentFromInputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String search(String ip) {
        Searcher searcher;
        try {
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            return RandomUtils.randomEle(regions);
        }

        String region;
        try {
            region = searcher.search(ip);
        } catch (Exception e) {
            return RandomUtils.randomEle(regions);
        }

        var splits = region.split(StringUtils.VERTICAL_BAR_REGEX);
        var list = new ArrayList<String>();
        for (var split : splits) {
            split = StringUtils.trim(split);
            if (split.equals("0")) {
                continue;
            }
            if (list.contains(split)) {
                continue;
            }
            list.add(split);
        }
        return StringUtils.joinWith(" ", list.toArray());
    }

}
