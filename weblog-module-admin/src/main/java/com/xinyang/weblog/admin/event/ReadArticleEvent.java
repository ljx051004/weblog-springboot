package com.xinyang.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
// TODO 这是什么 不明白
public class ReadArticleEvent extends ApplicationEvent {
    /**
     * 文章 ID
     */
    private Long articleId;

    public ReadArticleEvent(Object source,Long articleId) {
        super(source);
        this.articleId = articleId;
    }
}
