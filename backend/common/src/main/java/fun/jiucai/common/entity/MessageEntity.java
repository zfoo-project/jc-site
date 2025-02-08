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

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityCache
public class MessageEntity implements IEntity<Long> {

    /**
     * 消息的唯一id
     */
    @Id
    private long id;

    /**
     * 消息的类型
     */
    private byte type;

    /**
     * 发送者的id，ip地址转long
     */
    private long sendId;

    /**
     * 地域
     */
    private String region;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 发送的时间戳
     */
    private long timestamp;

    @Override
    public Long id() {
        return id;
    }

}
