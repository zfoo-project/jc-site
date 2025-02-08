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

package fun.jiucai.common.constant;

import com.zfoo.protocol.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author godotg
 */
public enum ChatAIEnum {

    all(-1, "all ai"),
    chatgpt(1, "openai-ChatGPT-3.5"),
    chatgpt4(4, "openai-ChatGPT-4"),

    xunfei(1000, "xunfei-星火"),
    baidu(2000, "百度-文心一言"),


    tencent(3000, "腾讯-混元"),
    alibaba(4000, "阿里-通义千问"),
    deepseek(5000, "DeepSeek"),

    llama(14000, "meta-羊驼"),

    google(15000, "gemini"),
    ;
    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    ChatAIEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private static Map<Integer, ChatAIEnum> aiMap = new HashMap<>();

    static {
        for (var aiEnum : ChatAIEnum.values()) {
            aiMap.put(aiEnum.getType(), aiEnum);
        }
    }

    public static ChatAIEnum typeOf(int type) {
        return aiMap.get(type);
    }

    @Override
    public String toString() {
        return StringUtils.format("type:{} name:{}", type, name);
    }
}
