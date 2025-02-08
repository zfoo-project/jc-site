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
 * 主要描述sd生成的文件的位置
 *
 * @author godotg
 */
public enum SdFileEnum {

    // 二次元
    TWO_DIMENSIONAL(0, "D:/a/{}.png"),
    TWO_DIMENSIONAL_1(1, "D:/aa/{}.png"),
    TWO_DIMENSIONAL_2(2, "D:/aaa/{}.png"),
    REALISM(10000, "D:/b/{}.png"),

    ;
    private int fileType;
    private String fileTemplate;

    public int getFileType() {
        return fileType;
    }

    public String getFileTemplate() {
        return fileTemplate;
    }

    SdFileEnum(int style, String fileTemplate) {
        this.fileType = style;
        this.fileTemplate = fileTemplate;
    }

    private static Map<Integer, SdFileEnum> fileTypeMap = new HashMap<>();

    static {
        for (var fileEnum : SdFileEnum.values()) {
            fileTypeMap.put(fileEnum.getFileType(), fileEnum);
        }
    }

    public static SdFileEnum sdFileType(int style) {
        return fileTypeMap.get(style);
    }

}
