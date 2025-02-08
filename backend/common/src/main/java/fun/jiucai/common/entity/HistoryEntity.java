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
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 */
@Data
@EntityCache
public class HistoryEntity implements IEntity<String> {

    // 财联社推送的历史记录
    public static final String CLS_HISTORY_ID = "cls";
    // 连接活跃的个数当天的历史记录
    public static final String IP_HISTORY_ID = "ip";

    public static final int SIZE_LIMIT_1000 = 1000;
    public static final int SIZE_LIMIT_5000 = 5000;

    @Id
    private String id;

    private List<Long> history = new ArrayList<>(1000);

    public void addHistory1000(long id) {
        if (history.size() >= SIZE_LIMIT_1000) {
            history.remove(0);
        }
        history.add(id);
    }

    public void addHistory5000(long id) {
        if (history.size() >= SIZE_LIMIT_5000) {
            history.remove(0);
        }
        history.add(id);
    }

    public void addHistoryUnique1000(long id) {
        if (history.contains(id)) {
            return;
        }
        if (history.size() >= SIZE_LIMIT_1000) {
            history.remove(0);
        }
        history.add(id);
    }

    public boolean addHistoryUnique5000(long id) {
        if (history.contains(id)) {
            return false;
        }
        if (history.size() >= SIZE_LIMIT_5000) {
            history.remove(0);
        }
        history.add(id);
        return true;
    }

    @Override
    public String id() {
        return id;
    }


}
