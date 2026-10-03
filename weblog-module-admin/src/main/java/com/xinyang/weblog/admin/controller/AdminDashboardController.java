package com.xinyang.weblog.admin.controller;

import com.xinyang.weblog.admin.service.AdminDashboardService;
import com.xinyang.weblog.common.aspect.ApiOperationLog;
import com.xinyang.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@Api(tags = "Admin 仪表盘")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    /**
     * 获取后台仪表盘基础统计信息
     * @return
     */
    @PostMapping("/statistics")
    @ApiOperation(value = "获取后台仪表盘基础统计信息")
    @ApiOperationLog(description = "获取后台仪表盘基础统计信息")
    public Response findDashboardStatistics(){
        return dashboardService.findDashboardStatistics();
    }
    /**
     * 获取文章发布热点接口开发
     * @return
     */
    @PostMapping("/publishArticle/statistics")
    @ApiOperation(value = "获取文章发布热点接口开发")
    @ApiOperationLog(description = "获取文章发布热点接口开发")
    public Response findDashboardPublishArticleStatistics() {
        return dashboardService.findDashboardPublishArticleStatistics();
    }
    /**
     * 获取后台仪表盘最近一周 PV 访问量信息
     * @return
     */
    @PostMapping("/pv/statistics")
    @ApiOperation(value = "获取后台仪表盘最近一周 PV 访问量信息")
    @ApiOperationLog(description = "获取后台仪表盘最近一周 PV 访问量信息")
    public Response findDashboardPVStatistics(){
        return dashboardService.findDashboardPVStatistics();
    }
}
