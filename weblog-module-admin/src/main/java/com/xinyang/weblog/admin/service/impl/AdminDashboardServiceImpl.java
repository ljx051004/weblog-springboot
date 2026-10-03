package com.xinyang.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xinyang.weblog.common.constant.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinyang.weblog.admin.model.vo.dashboard.FindDashboardPVStatisticsInfoRspVO;
import com.xinyang.weblog.admin.model.vo.dashboard.FindDashboardStatisticsInfoRspVO;
import com.xinyang.weblog.admin.service.AdminDashboardService;
import com.xinyang.weblog.common.domain.dos.ArticleDO;
import com.xinyang.weblog.common.domain.dos.ArticlePublishCountDO;
import com.xinyang.weblog.common.domain.dos.StatisticsArticlePVDO;
import com.xinyang.weblog.common.domain.mapper.ArticleMapper;
import com.xinyang.weblog.common.domain.mapper.CategoryMapper;
import com.xinyang.weblog.common.domain.mapper.StatisticsArticlePVMapper;
import com.xinyang.weblog.common.domain.mapper.TagMapper;
import com.xinyang.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;

    /**
     * 获取后台仪表盘基础统计信息
     * @return
     */
    @Override
    public Response findDashboardStatistics() {
        // 获取文章总数
        Long articleTotalCount = articleMapper.selectCount(null);

        // 查询分类总数
        Long categoryTotalCount = categoryMapper.selectCount(Wrappers.emptyWrapper());

        // 查询标签总数
        Long tagTotalCount = tagMapper.selectCount(Wrappers.emptyWrapper());

        // 获取总浏览量
        List<ArticleDO> articleDOS = articleMapper.selectAllReadNum();
        // TODO pvTotalCount什么意思
        Long pvTotalCount = 0L;

        if(!CollectionUtils.isEmpty(articleDOS)){
            // 计算总浏览量
            pvTotalCount = articleDOS.stream().mapToLong(ArticleDO::getReadNum).sum();
        }
        // 返回VO
        FindDashboardStatisticsInfoRspVO findDashboardStatisticsInfoRspVO=FindDashboardStatisticsInfoRspVO.builder()
                .articleTotalCount(articleTotalCount)
                .categoryTotalCount(categoryTotalCount)
                .tagTotalCount(tagTotalCount)
                .pvTotalCount(pvTotalCount)
                .build();
        return Response.success(findDashboardStatisticsInfoRspVO);
    }

    /**
     * 获取文章发布热点接口开发
     * @return
     */
    @Override
    public Response findDashboardPublishArticleStatistics() {
        // 当前日期
        LocalDate currDate  = LocalDate.now();

        //  当前日期倒退一年的日期
        LocalDate startDate  = currDate.minusYears(1);

        // 查找这一年内，每日发布的文章数量
        // TODO 这是什么意思
        List<ArticlePublishCountDO> articlePublishCountDOS  = articleMapper.selectDateArticlePublishCount(startDate,currDate.plusDays(1));

        // 因为查询出来的sql是date：count
        // 所以选择用map来存储
        Map<LocalDate, Long> map = null;
        if(!CollectionUtils.isEmpty(articlePublishCountDOS )){
            // todo 从这里开始看不懂了
            Map<LocalDate,Long> dateArticleCountMap =articlePublishCountDOS.stream()
                    .collect(Collectors.toMap(ArticlePublishCountDO::getDate, ArticlePublishCountDO::getCount));
            // 有序 Map, 返回的日期文章数需要以升序排列
            map =  Maps.newLinkedHashMap();
            // 从上一年的今天循环到今天
            for (; startDate.isBefore(currDate) || startDate.isEqual(currDate); startDate = startDate.plusDays(1)) {
                // 以日期作为 key 从 dateArticleCountMap 中取文章发布总量
                Long count = dateArticleCountMap.get(startDate);
                // 设置到返参 Map
                map.put(startDate, Objects.isNull(count) ? 0 : count);
            }
        }
        return Response.success(map);
    }

    /**
     * 获取后台仪表盘最近一周 PV 访问量信息
     * @return
     */
    @Override
    public Response findDashboardPVStatistics() {
        // 查询最近一周的pv访问量记录
        List<StatisticsArticlePVDO> articlePVDOS  = articlePVMapper.selectLatestWeekRecords();

        Map<LocalDate,Long> pvDateCountMap = Maps.newHashMap();
        if(!CollectionUtils.isEmpty(articlePVDOS)){
            // 转 Map, 方便后续通过日期获取 PV 访问量
            // key: 日期, value: PV 访问量
            pvDateCountMap= articlePVDOS.stream().collect(Collectors.toMap(
                    StatisticsArticlePVDO::getPvDate, StatisticsArticlePVDO::getPvCount));

        }
        // todo 这里之后全都没有看懂
        FindDashboardPVStatisticsInfoRspVO vo = null;

        // 日期集合
        List<String> pvDates = Lists.newArrayList();
        // PV 集合
        List<Long> pvCounts = Lists.newArrayList();

        // 当前日期
        LocalDate currDate = LocalDate.now();
        // 一周前
        LocalDate tmpDate = currDate.minusWeeks(1);
        // 从一周前开始循环
        for (; tmpDate.isBefore(currDate) || tmpDate.isEqual(currDate); tmpDate = tmpDate.plusDays(1)) {
            // 设置对应日期的 PV 访问量
            pvDates.add(tmpDate.format(Constants.MONTH_DAY_FORMATTER));
            Long pvCount = pvDateCountMap.get(tmpDate);
            pvCounts.add(Objects.isNull(pvCount) ? 0 : pvCount);
        }

        vo = FindDashboardPVStatisticsInfoRspVO.builder()
                .pvDates(pvDates)
                .pvCounts(pvCounts)
                .build();

        return Response.success(vo);
    }
}
