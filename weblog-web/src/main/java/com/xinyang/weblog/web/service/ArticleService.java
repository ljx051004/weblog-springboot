package com.xinyang.weblog.web.service;

import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.xinyang.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;

public interface ArticleService  {
    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);

    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);
}
