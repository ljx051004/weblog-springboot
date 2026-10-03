package com.xinyang.weblog.admin.controller;

import com.xinyang.weblog.admin.model.vo.article.*;
import com.xinyang.weblog.admin.service.AdminArticleService;
import com.xinyang.weblog.common.aspect.ApiOperationLog;
import com.xinyang.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/article")
@Api(tags = "文章管理")
public class AdminArticleController {
    @Autowired
    private AdminArticleService adminArticleService;

    /**
     * 发布文章
     * @param publishArticleReqVO
     * @return
     */
    @PostMapping("/publish")
    @ApiOperation("发布文章")
    @ApiOperationLog(description = "发布文章")
    //TODO 这是什么意思
    @PreAuthorize("hasRole('ROLE_ADMIN')")// 表示需要有ROLE_ADMIN角色才能访问
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO){
        return adminArticleService.publishArticle(publishArticleReqVO);
    }

    /**
     * 删除文章
     * @param deleteArticleReqVO
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("删除文章")
    @ApiOperationLog(description = "删除文章")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO){
        return adminArticleService.deleteArticle(deleteArticleReqVO);
    }

    /**
     * 查询文章分页数据
     * @param findArticlePageListReqVO
     * @return
     */
    // todo 为什么全是post请求
    @PostMapping("/list")
    @ApiOperation(value = "查询文章分页数据")
    @ApiOperationLog(description = "查询文章分页数据")
    public Response findArticleList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO){
        return adminArticleService.findArticleList(findArticlePageListReqVO);
    }

    /**
     * 查询文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    @PostMapping("/detail")
    @ApiOperation("查询文章详情")
    @ApiOperationLog(description = "查询文章详情")
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticleDetailReqVO){
        return adminArticleService.findArticleDetail(findArticleDetailReqVO);
    }

    /**
     * 更新文章
     * @param updateArticleReqVO
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更新文章")
    @ApiOperationLog(description = "更新文章")
    public Response updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO){
        return adminArticleService.updateArticle(updateArticleReqVO);
    }

    /**
     * 查询标签 Select 列表数据
     * @return
     */
    @PostMapping("/select/list")
    @ApiOperation(value = "查询标签 Select 列表数据")
    @ApiOperationLog(description = "查询标签 Select 列表数据")
    public Response findTagSelectList(){
        return adminArticleService.findTagSelectList();
    }
}
