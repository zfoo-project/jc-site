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

/**
 * @author godotg
 */
public enum MidjourneyTaskType {

    IMAGINE,

    REROLL,

    UPSAMPLE,

    VARIATION,

    UPSCALE,

    ZOOM,

    ;

    public static MidjourneyTaskType typeBySimpleContent(String content) {
        if (content.contains("- Variations")) {
            return VARIATION;
        } else if (content.contains("- Image")) {
            return UPSAMPLE;
        }
        return null;
    }
}
