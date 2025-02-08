/*
 * Copyright (C) 2020 The zfoo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package fun.jiucai.cloud;

import com.zfoo.event.model.AppStartEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2021-01-20 16:00
 */
@SpringBootApplication(scanBasePackages = "fun.jiucai",
        exclude = {
                // 排除MongoDB自动配置
                MongoDataAutoConfiguration.class,
                MongoRepositoriesAutoConfiguration.class,
                MongoAutoConfiguration.class
        })
public class Cloud {

    public static void main(String[] args) {
        var context = SpringApplication.run(Cloud.class, args);
        context.registerShutdownHook();
        context.publishEvent(new AppStartEvent(context));
    }

}
