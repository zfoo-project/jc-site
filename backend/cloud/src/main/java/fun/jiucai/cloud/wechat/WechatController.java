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

package fun.jiucai.cloud.wechat;

import com.zfoo.event.manager.EventBus;
import com.zfoo.protocol.util.*;
import fun.jiucai.cloud.wechat.event.WeChatMessageEvent;
import fun.jiucai.cloud.wechat.model.WeChatMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author godotg
 */
@Slf4j
@Controller
@CrossOrigin
public class WechatController {


    @SneakyThrows
    @RequestMapping("/api/wechat/message")
    public void excelHotswap(HttpServletRequest request, HttpServletResponse response) {
        // 接收微信服务器以Get请求发送的4个参数
        var signature = request.getParameter("signature");
        var timestamp = request.getParameter("timestamp");
        var nonce = request.getParameter("nonce");
        var echostr = request.getParameter("echostr");
        log.info("wechat signature:[{}] timestamp:[{}] nonce:[{}] echostr:[{}]", signature, timestamp, nonce, echostr);

        var out = response.getWriter();
        if (!checkSignature(signature, timestamp, nonce)) {
            log.error("不是微信发来的请求！");
            return;
        }
        // 校验通过，原样返回echostr参数内容
        out.write(StringUtils.trim(echostr));

        var requestBoy = StringUtils.bytesToString(IOUtils.toByteArray(request.getInputStream()));
        var weChatMessage = DomUtils.string2Object(requestBoy, WeChatMessage.class);
        log.info(JsonUtils.object2String(weChatMessage));
        EventBus.post(new WeChatMessageEvent(weChatMessage));
    }

    @RequestMapping("/MP_verify_pvpKrM8ydb0ga79u.txt")
    @ResponseBody
    public String verify(HttpServletRequest request, HttpServletResponse response) {
        return ClassUtils.getFileFromClassPathToString("/pay/MP_verify_pvpKrM8ydb0ga79u.txt");
    }


    //这个token值要和服务器配置一致
    private static final String MP_TOKEN = "your-token";

    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[]{MP_TOKEN, timestamp, nonce};
        // 排序
        Arrays.sort(arr);
        // 生成字符串
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        // 使用commons codec生成sha1字符串
        String shaStr = DigestUtils.shaHex(content.toString());
        // 与微信传递过来的签名进行比较
        return shaStr.equals(signature);
    }

}