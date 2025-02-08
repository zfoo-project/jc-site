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

import com.zfoo.net.NetContext;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import fun.jiucai.common.protocol.auth.OssPolicyRequest;
import fun.jiucai.common.protocol.auth.OssPolicyResponse;
import fun.jiucai.home.manager.OssManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author godotg
 */
@Slf4j
@Component
public class OssController {

    @Autowired
    private OssManager ossManager;

    @PacketReceiver
    public void atOssPolicyRequest(Session session, OssPolicyRequest request) {
        var ossPolicy = ossManager.policy();
        var response = new OssPolicyResponse(ossPolicy);
        NetContext.getRouter().send(session, response);
    }

}
