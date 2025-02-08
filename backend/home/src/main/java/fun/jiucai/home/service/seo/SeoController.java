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

package fun.jiucai.home.service.seo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zfoo.net.anno.PacketReceiver;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.protocol.util.RandomUtils;
import fun.jiucai.home.chatai.ChatgptOpenAiService;
import fun.jiucai.common.protocol.broker.SeoAsk;
import fun.jiucai.common.protocol.news.News;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author godotg
 */
@Slf4j
@Component
public class SeoController {

    private static final String PROMPT_KEYWORDS = "给你一篇文章，总结5个关键词，并且给出另外的5个同义词。";
    private static final String PROMPT_DESCRIPTION = "我要你担任观察员。我将为您提供与新闻相关的故事或主题，您将用简洁的语句总结文章的主要内容。";
    private static final String PROMPT_COMMENT = "我要你担任评论员。我将为您提供与新闻相关的故事或主题，您将撰写一篇评论文章，对手头的主题提供有见地的评论。您应该利用自己的经验，从金融，科技，人文，历史，政治，军事，全球局势各个不同角度分析，深思熟虑地解释为什么某事很重要，用事实支持主张，并讨论故事中出现的任何问题的潜在解决方案。";
    private static final String PROMPT_TITLE = "将下面这段话用近义词替换并且更加简介的输出：";
    private static final String PROMPT_CONTENT = "将下面这段话用近义词替换并且更加简介的输出：";

    private static final int RETRY_TIMES = 3;

    @Autowired
    private SeoService seoService;
    @Autowired
    private ChatgptOpenAiService openAiService;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NewsSeo {
        private News news;
        private String image;
        private String keywords;
        private String description;
        private String comment;
    }

    @PacketReceiver
    public void atSeoAsk(Session session, SeoAsk ask) throws IOException {
        if (true) {
            return;
        }
        var news = ask.getNews();

        var seoTemplate = StringUtils.bytesToString(IOUtils.toByteArray(ClassUtils.getFileFromClassPath("seo-template.html")));

        var keywords = openAiService.choiceWithRetry(PROMPT_KEYWORDS, news.getContent(), RETRY_TIMES);
        var description = openAiService.choiceWithRetry(PROMPT_DESCRIPTION, news.getContent(), RETRY_TIMES);
        var comment = openAiService.choiceWithRetry(PROMPT_COMMENT, news.getContent(), RETRY_TIMES);

        // 优化chatgpt输出的内容
        keywords = keywords.replaceAll("关键词：", "");
        keywords = keywords.replaceAll("关键词", "");
        keywords = keywords.replaceAll("同义词：", "");
        keywords = keywords.replaceAll("同义词", "");

        var title = StringUtils.isEmpty(news.getTitle()) ? keywords : openAiService.choiceWithRetry(PROMPT_TITLE, news.getTitle(), RETRY_TIMES);
        var elements = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(news.getStocks())) {
            news.getStocks().stream().map(it -> it.getName()).forEach(it -> elements.add(it));
        }
        if (CollectionUtils.isNotEmpty(news.getConcepts())) {
            news.getConcepts().stream().map(it -> it.getName()).forEach(it -> elements.add(it));
        }
        if (CollectionUtils.isNotEmpty(news.getSubjects())) {
            news.getSubjects().stream().forEach(it -> elements.add(it));
        }
        var elementStr = StringUtils.joinWith("，", elements.toArray());
        var image = RandomUtils.randomInt(1, 800);
        var html = StringUtils.format(seoTemplate, title, keywords, description, title, description, elementStr, image, comment);
        seoService.uploadHtml(news.getId(), html);
        seoService.uploadJson(news.getId(), JsonUtils.object2String(new NewsSeo(news, String.valueOf(image), keywords, description, comment)));
        seoService.baiduSeo(news.getId());
    }


}
