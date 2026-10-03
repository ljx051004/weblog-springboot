package com.xinyang.weblog.admin.service;

import com.xinyang.weblog.common.utils.Response;

public interface AdminDashboardService {
    /**
     * 获取后台仪表盘基础统计信息
     * @return
     */
    Response findDashboardStatistics();

    /**
     * 获取文章发布热点接口开发
     * @return
     */
    Response findDashboardPublishArticleStatistics();

    /**
     * 获取后台仪表盘最近一周 PV 访问量信息
     * @return
     */
    Response findDashboardPVStatistics();

}
