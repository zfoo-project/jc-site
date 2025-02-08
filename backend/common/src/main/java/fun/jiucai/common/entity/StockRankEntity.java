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
import com.zfoo.scheduler.util.TimeUtils;
import lombok.Data;

/**
 * @author godotg
 */
@Data
@EntityCache
public class StockRankEntity implements IEntity<Integer> {

    @Id
    private int id;

    private String name;

    private int rankThs;
    // 最近一周进入排名100的时间
    private long ranThsTime;

    private int rankEastMoney;
    private long rankEastMoneyTime;


    @Override
    public Integer id() {
        return id;
    }

    public static final long RANK_RECORD_TIME = TimeUtils.MILLIS_PER_WEEK * 2;
    public static final long RANK_PRIMARY_TIME = TimeUtils.MILLIS_PER_DAY * 3;

    public boolean primaryRankThs() {
        var now = TimeUtils.now();
        if (now - ranThsTime > RANK_RECORD_TIME) {
            ranThsTime = now;
        }
        return now - ranThsTime < RANK_PRIMARY_TIME;
    }

    public boolean primaryRankEastMoney() {
        var now = TimeUtils.now();
        if (now - rankEastMoneyTime > RANK_RECORD_TIME) {
            rankEastMoneyTime = now;
        }
        return now - rankEastMoneyTime < RANK_PRIMARY_TIME;
    }

}
