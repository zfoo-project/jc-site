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
import com.zfoo.orm.anno.IndexText;
import com.zfoo.orm.model.IEntity;
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
public class SdImageEntity implements IEntity<Long> {

    @Id
    private long id;

    // 图片的风格，0->二次元
    private int style;
    // 图片的尺寸，0->768*768，1->768*1024
    private int dimension;
    // steps/10取整
    private int step;
    @IndexText
    private String prompt;

    private String path;

    @Override
    public Long id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SdImageEntity that = (SdImageEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
