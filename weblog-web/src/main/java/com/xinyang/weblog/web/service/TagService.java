package com.xinyang.weblog.web.service;

import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.xinyang.weblog.web.model.vo.tag.FindTagArticlePageListRspVO;

public interface TagService {
    /**
     * 前台获取标签列表
     * @return
     */
    Response findTagList();

    /**
     * 前台获取标签下文章列表
     * @return
     */
    Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO);
}
