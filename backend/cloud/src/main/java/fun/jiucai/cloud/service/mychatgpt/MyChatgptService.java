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

package fun.jiucai.cloud.service.mychatgpt;

import com.zfoo.event.manager.EventBus;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.RandomUtils;
import com.zfoo.scheduler.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author godotg
 */
@Slf4j
@Component
public class MyChatgptService {

    @Autowired
    private MyChatgptConfiguration myChatgptConfiguration;

    private List<MyOpenAi> myOpenAis = new CopyOnWriteArrayList<>();

    private volatile long lastCheckUsageTime;

    @PostConstruct
    public void init() {
        for (var mytoken : myChatgptConfiguration.getMytokens()) {
            var myOpenAi = new MyOpenAi(mytoken.getUrl(), mytoken.getToken(),  Duration.ofSeconds(30));
            myOpenAis.add(myOpenAi);
        }
        checkUsage();
    }

    @Scheduler(cron = "0 0/20 * * * ?")
    public void cronUsage() {
        EventBus.asyncExecute(() -> checkUsage());
    }

    @SneakyThrows
    public void checkUsage() {
        var now = TimeUtils.now();
        // 1分钟之内最多只校验一次使用量
        if (now - lastCheckUsageTime < TimeUtils.MILLIS_PER_MINUTE) {
            return;
        }
        lastCheckUsageTime = now;
        for (var myOpenAi : myOpenAis) {
            var usage = myOpenAi.usage();
            if (usage.getRemain_quota() > 0.01D) {
                log.info("token:[{}] usage:[{}]", myOpenAi.token(), usage);
                continue;
            }
            log.info("not enough token:[{}] usage:[{}]", myOpenAi.token(), usage);
            close(myOpenAi);
        }
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(myOpenAis);
    }

    public MyOpenAi myOpenAi() {
        return RandomUtils.randomEle(myOpenAis);
    }

    public void close(MyOpenAi myOpenAi) {
        log.error("close myopenai token:[{}]", myOpenAi.token());
        myOpenAis.remove(myOpenAi);
        myOpenAi.shutdownExecutor();
    }
}
