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
import fun.jiucai.common.protocol.stock.Market;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityCache
public class MarketEntity implements IEntity<Long> {

    @Id
    private long id;

    private long date;

    private int stockNum;

    private int stockNum0;

    private int stockNumNeg005;

    private int stockNumNeg10;

    private long totalPrice;

    private long marketIndex;
    private long shMarketIndex;
    private long kcMarketIndex;
    private long szMarketIndex;
    private long cyMarketIndex;
    private long bjMarketIndex;


    private long exchange;

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

    @Override
    public Long id() {
        return id;
    }

    public Market toMarket() {
        return new Market(date, stockNum, stockNum0, stockNumNeg005, stockNumNeg10, totalPrice
                , marketIndex / 1_0000_0000, shMarketIndex / 1_0000_0000, kcMarketIndex / 1_0000_0000
                , szMarketIndex / 1_0000_0000, cyMarketIndex / 1_0000_0000, bjMarketIndex / 1_0000_0000
                , exchange / 1_0000_0000, amount / 1_0000_0000
                , shExchange / 1_0000_0000, shAmount / 1_0000_0000
                , kcExchange / 1_0000_0000, kcAmount / 1_0000_0000
                , szExchange / 1_0000_0000, szAmount / 1_0000_0000
                , cyExchange / 1_0000_0000, cyAmount / 1_0000_0000
                , bjExchange / 1_0000_0000, bjAmount / 1_0000_0000);
    }

}
