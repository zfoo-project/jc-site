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

package fun.jiucai.cloud.wechat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;


/**
 * @author godotg
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeChatMessage {

    /**
     * 事件类型，subscribe(订阅)、unsubscribe(取消订阅)
     */
    @JacksonXmlProperty(localName = "Event")
    private String event;

    /**
     * 事件 KEY 值，是一个32位无符号整数，即创建二维码时的二维码scene_id
     */
    @JacksonXmlProperty(localName = "EventKey")
    private String eventKey;

    /**
     * 文本消息内容
     */
    @JacksonXmlProperty(localName = "Content")
    private String content;

    /**
     * 消息类型，(text,event)
     */
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;

    /**
     * 开发者微信号
     */
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;

    /**
     * fromUserName为关注人的openId
     */
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;

    /**
     * 消息创建时间 （整型）
     */
    @JacksonXmlProperty(localName = "CreateTime")
    private long createTime;

    /**
     * 消息创建时间 （整型）
     */
    @JacksonXmlProperty(localName = "Ticket")
    private String ticket;
}
