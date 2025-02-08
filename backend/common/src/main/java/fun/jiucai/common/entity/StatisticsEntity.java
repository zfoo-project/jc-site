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

package fun.jiucai.common.entity;

import com.zfoo.orm.anno.EntityCache;
import com.zfoo.orm.anno.Id;
import com.zfoo.orm.model.IEntity;
import fun.jiucai.common.protocol.admin.NewsStat;
import fun.jiucai.common.protocol.admin.Statistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@EntityCache
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsEntity implements IEntity<Long> {

    // id为当天最后一秒的时间戳
    @Id
    private long id;
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
    private NewsStat newsStat = new NewsStat();

    @Override
    public Long id() {
        return id;
    }

    public Statistics toSystem() {
        return new Statistics(id, ips, active, newsRequest, newsSearchRequest, chatgptRequest, googleSearch, bingSearch, weixinSearch, bilibiliSearch
                , midImagineRequest, sdSimulateRequest, animationRequest, navigation, newsStat);
    }
}
