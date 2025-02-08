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

package fun.jiucai.cloud.statistic.event;

import com.zfoo.event.model.IEvent;
import com.zfoo.net.session.Session;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author godotg
 */
@Data
@AllArgsConstructor
public class StatAnimationRequestEvent implements IEvent {
    private Session session;
}
