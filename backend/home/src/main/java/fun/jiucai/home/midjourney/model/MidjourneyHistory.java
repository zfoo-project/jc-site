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

package fun.jiucai.home.midjourney.model;

import lombok.Data;

/**
 * @author godotg
 */
@Data
public class MidjourneyHistory {

    private long requestSid;

    private long discordMessageId;

    private String nonce;

    private String prompt;

    private String imageRealName;

    public static MidjourneyHistory valueOf(long requestSid, long discordMessageId, String nonce, String prompt, String imageRealName) {
        var history = new MidjourneyHistory();
        history.requestSid = requestSid;
        history.discordMessageId = discordMessageId;
        history.nonce = nonce;
        history.prompt = prompt;
        history.imageRealName = imageRealName;
        return history;
    }
}
