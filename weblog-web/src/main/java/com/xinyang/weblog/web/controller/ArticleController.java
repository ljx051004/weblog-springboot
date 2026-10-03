package com.xinyang.weblog.web.controller;

import com.xinyang.weblog.common.aspect.ApiOperationLog;
import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.xinyang.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.xinyang.weblog.web.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags="文章")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    @PostMapping("/article/list")
    @ApiOperation(value = "获取首页文章分页数据")
    @ApiOperationLog(description = "获取首页文章分页数据")
    public Response findArticlePageList(@RequestBody @Validated FindIndexArticlePageListReqVO findIndexArticlePageListReqVO){
        return articleService.findArticlePageList(findIndexArticlePageListReqVO);
    }

    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    @PostMapping("/article/detail")
    @ApiOperation(value = "获取文章详情")
    @ApiOperationLog(description = "获取文章详情")
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticleDetailReqVO){
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }

}
