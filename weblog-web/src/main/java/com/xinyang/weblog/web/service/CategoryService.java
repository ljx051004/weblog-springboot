package com.xinyang.weblog.web.service;

import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;

public interface CategoryService {
    /**
     * 前台获取分类列表
     * @return
     */
    Response findCategoryList();

    /**
     * 前台获取分类下文章分页列表
     * @param findCategoryArticlePageListReqVO
     * @return
     */
    Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO);
}
