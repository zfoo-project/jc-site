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

import com.zfoo.protocol.util.StringUtils;
import lombok.Data;

/**
 * @author godotg
 */
@Data
public class MidjourneyTask {

    private MidjourneyTaskType taskType;
    private long requestSid;
    private String nonce;
    private String prompt;
    private String promptWith;

    private long startTime;
    private long discordMessageId;
    private String imageRealName;
    private int selectIndex;
    private String category;
    private String zoom;

    public static MidjourneyTask valueOf(MidjourneyTaskType taskType, long requestSid, String nonce, String prompt) {
        var task = new MidjourneyTask();
        task.taskType = taskType;
        task.requestSid = requestSid;
        task.nonce = nonce;
        task.prompt = prompt;
        task.promptWith = prompt;
        return task;
    }

    public boolean similarSimple(String content) {
        if (content.contains(prompt) || content.contains(promptWith)) {
            return true;
        }
        var simplePromptWith = promptWith;

        // -----------------------------------------------------------------------------------------------------------
        simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }

        simplePromptWith = StringUtils.substringAfterFirst(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }

        // -----------------------------------------------------------------------------------------------------------
        simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }

        simplePromptWith = StringUtils.substringAfterFirst(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }

        return false;
    }

    public boolean similarNormal(String content) {
        if (content.contains(prompt) || content.contains(promptWith)) {
            return true;
        }
        var simplePromptWith = promptWith;

        // -----------------------------------------------------------------------------------------------------------
        simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }
        simplePromptWith = StringUtils.substringAfterFirst(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }
        simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }
        simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }
        // -----------------------------------------------------------------------------------------------------------
        simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }
        simplePromptWith = StringUtils.substringAfterFirst(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }
        simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }

        // -----------------------------------------------------------------------------------------------------------
        simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
        if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
            return true;
        }
        return false;
    }

    public boolean similarComplex(String content) {
        if (content.contains(prompt) || content.contains(promptWith)) {
            return true;
        }
        var noSpaceContent = content.replaceAll(StringUtils.SPACE_REGEX, "");
        var simplePromptWith = promptWith;
        var simplePrompt = prompt;

        for (int i = 0; i < 10; i++) {
            // -----------------------------------------------------------------------------------------------------------
            simplePromptWith = StringUtils.substringAfterFirst(simplePromptWith, StringUtils.SPACE);
            if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
                return true;
            }
            simplePromptWith = StringUtils.substringBeforeLast(simplePromptWith, StringUtils.SPACE);
            if (StringUtils.isNotBlank(simplePromptWith) && content.contains(simplePromptWith)) {
                return true;
            }

            // ----------------------------------------------------------------------------------------------------------
            simplePrompt = StringUtils.substringAfterFirst(simplePrompt, StringUtils.SPACE);
            if (StringUtils.isNotBlank(simplePrompt) && content.contains(simplePrompt)) {
                return true;
            }
            simplePrompt = StringUtils.substringBeforeLast(simplePrompt, StringUtils.SPACE);
            if (StringUtils.isNotBlank(simplePrompt) && content.contains(simplePrompt)) {
                return true;
            }

            // ----------------------------------------------------------------------------------------------------------
            var noSpaceSimplePromptWith = simplePromptWith.replaceAll(StringUtils.SPACE_REGEX, "");
            if (noSpaceContent.contains(noSpaceSimplePromptWith)) {
                return true;
            }
        }
        return false;
    }
}
