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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@EntityCache
public class NewsEntity implements IEntity<Long> {

    @Id
    private long id;

    private byte level;

    private String title;

    private String content;

    private long ctime;

    private List<Stock> stocks = new ArrayList<>();
    private List<Concept> concepts = new ArrayList<>();
    private List<String> subjects = new ArrayList<>();


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stock {
        private int code;
        private float price;
        private float rise;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Concept {
        private int code;
        private float rise;
    }


    @Override
    public Long id() {
        return id;
    }


}
