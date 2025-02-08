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

package fun.jiucai.cloud.model.event;

import com.zfoo.event.model.IEvent;
import com.zfoo.net.session.Session;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author godotg
 */
@Data
@AllArgsConstructor
public class ChatgptEvent implements IEvent {

    private Session session;
    private long requestSid;
    private long requestId;
    private List<ChatgptMessage> messages;
    private Set<Integer> ignoreAIs;
    private boolean googleSearch;
    private boolean bingSearch;
    private boolean weixinSearch;
    private boolean bilibiliSearch;

}
