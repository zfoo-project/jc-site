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

package fun.jiucai.home.controller;

import com.zfoo.event.manager.EventBus;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import fun.jiucai.home.manager.OssManager;
import fun.jiucai.common.protocol.chat.GroupChatNotice;
import fun.jiucai.common.protocol.sdiffusion.ImageDeleteAsk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author godotg
 */
@Slf4j
@Component
public class ImageController {

    @Autowired
    private OssManager ossManager;

    @PacketReceiver
    public void atImageDeleteAsk(Session session, ImageDeleteAsk ask) {
        // 延迟20s删除图片，避免图片没有下载完就删除
        SchedulerBus.schedule(() -> {
            EventBus.asyncExecute(() -> {
                var realUrl = ask.getRealUrl();
                var objectName = StringUtils.substringAfterFirst(realUrl, "https://jiucai.fun/");
                ossManager.delete(objectName);
                log.info("delete image [{}]", realUrl);
            });

        }, 20, TimeUnit.SECONDS);
    }

    @PacketReceiver
    public void atGroupChatNotice(Session session, GroupChatNotice notice) {
    }

}
