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

import java.util.HashMap;
import java.util.Map;

/**
 * @author godotg
 */
public enum SdStyleEnum {

    // 二次元
    TWO_DIMENSIONAL(0),
    REALISM(1),

    ;
    private int style;

    public int getStyle() {
        return style;
    }

    SdStyleEnum(int style) {
        this.style = style;
    }

    private static Map<Integer, SdStyleEnum> styleMap = new HashMap<>();

    static {
        for (var styleEnum : SdStyleEnum.values()) {
            styleMap.put(styleEnum.getStyle(), styleEnum);
        }
    }

    public static SdStyleEnum sdStyle(int style) {
        return styleMap.get(style);
    }

}
