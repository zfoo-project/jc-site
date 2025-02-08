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

package fun.jiucai.cloud.util.xdb;

public class Header {
    public final int version;
    public final int indexPolicy;
    public final int createdAt;
    public final int startIndexPtr;
    public final int endIndexPtr;
    public final byte[] buffer;

    public Header(byte[] buff) {
        assert buff.length >= 16;
        version = Searcher.getInt2(buff, 0);
        indexPolicy = Searcher.getInt2(buff, 2);
        createdAt = Searcher.getInt(buff, 4);
        startIndexPtr = Searcher.getInt(buff, 8);
        endIndexPtr = Searcher.getInt(buff, 12);
        buffer = buff;
    }

    @Override public String toString() {
        return "{" +
            "Version: " + version + ',' +
            "IndexPolicy: " + indexPolicy + ',' +
            "CreatedAt: " + createdAt + ',' +
            "StartIndexPtr: " + startIndexPtr + ',' +
            "EndIndexPtr: " + endIndexPtr +
        '}';
    }
}
