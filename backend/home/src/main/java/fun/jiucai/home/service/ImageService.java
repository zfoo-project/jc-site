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

package fun.jiucai.home.service;

import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.scheduler.util.TimeUtils;
import fun.jiucai.common.entity.ImageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ImageService {

    public void imageHistory(String imageUrl, String imageUrlLow, String imageUrlMiddle, String imageUrlHigh) {
        var id = MongoIdUtils.getIncrementIdFromMongoDefault(ImageEntity.class);
        var entity = new ImageEntity(id, imageUrl, imageUrlLow, imageUrlMiddle, imageUrlHigh, TimeUtils.now());
        OrmContext.getAccessor().insert(entity);
    }

}
