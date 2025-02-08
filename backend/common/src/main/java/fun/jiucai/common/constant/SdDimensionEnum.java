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

import com.zfoo.protocol.util.RandomUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author godotg
 */
public enum SdDimensionEnum {

    DIMENSION_768_768(0, 768, 768),
    DIMENSION_768_1024(1, 768, 1024),

    DIMENSION_RANDOM(99, 512, 512),
    ;
    private int dimension;
    private int width;
    private int height;
    private int area;

    public int getDimension() {
        return dimension;
    }

    public int getArea() {
        return area;
    }

    SdDimensionEnum(int dimension, int width, int height) {
        this.dimension = dimension;
        this.width = width;
        this.height = height;
        this.area = width * height;
    }

    private static Map<Integer, SdDimensionEnum> dimensionMap = new HashMap<>();
    private static Map<Integer, SdDimensionEnum> arenaMap = new HashMap<>();

    static {
        for (var dimensionEnum : SdDimensionEnum.values()) {
            dimensionMap.put(dimensionEnum.getDimension(), dimensionEnum);
            arenaMap.put(dimensionEnum.getArea(), dimensionEnum);
        }
    }

    public static SdDimensionEnum sdDimension(int dimension) {
        return dimensionMap.get(dimension);
    }

    public static SdDimensionEnum sdDimensionByArena(int arena) {
        return arenaMap.get(arena);
    }

    public static SdDimensionEnum randomDimension() {
        var random = RandomUtils.randomInt(10);
        if (random < 3) {
            return DIMENSION_768_768;
        } else {
            return DIMENSION_768_1024;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
