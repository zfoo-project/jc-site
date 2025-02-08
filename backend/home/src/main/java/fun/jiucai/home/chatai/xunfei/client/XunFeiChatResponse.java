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

package fun.jiucai.home.chatai.xunfei.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author godotg
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class XunFeiChatResponse {

    private Header header;
    private Payload payload;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        // 错误码，0表示正常，非0表示出错；详细释义可在接口说明文档最后的错误码说明了解
        private int code;
        // 会话是否成功的描述信息
        private String message;
        // 会话的唯一id，用于讯飞技术人员查询服务端会话日志使用,出现调用错误时建议留存该字段
        private String sid;
        // 会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果
        private int status;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private Choices choices;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choices {
        private int status;
        private int seq;
        private List<Text> text;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Text {
        private int index;
        private String content;
        private String role;
    }

}
