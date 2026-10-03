package com.xinyang.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyang.weblog.common.domain.dos.ArticleDO;
import com.xinyang.weblog.common.domain.dos.ArticlePublishCountDO;
import com.xinyang.weblog.common.utils.PageResponse;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
// TODO 这个项目中的所有分页查询都要记住
public interface ArticleMapper extends BaseMapper<ArticleDO> {
    /**
     * 分页查询
     * @param current
     * @param size
     * @param title
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<ArticleDO> selectArticleList(Long current, Long size, String title, LocalDate startDate, LocalDate endDate){
        Page<ArticleDO> page = new Page<>(current, size);

        // 构造查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title) // like 模块查询
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate) // 大于等于 startDate)
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate) // 小于等于 endDate)
                .orderByDesc(ArticleDO::getCreateTime); // 按创建时间倒序

        return selectPage(page, wrapper);

    }

    /**
     * 根据文章 ID 批量分页查询
     * @param current
     * @param size
     * @param articleIds
     * @return
     */
    // TODO 这个要背会
    default Page<ArticleDO> selectPageListByArticleIds(List<Long> articleIds, Long current, Long size){
        // 分页对象（查询第几页，每页多少数据）
        Page<ArticleDO> page = new Page<>(current, size);

        // 构造查询条件
        return selectPage(page, Wrappers.<ArticleDO>lambdaQuery()
                .in(ArticleDO::getId, articleIds)
                .orderByDesc(ArticleDO::getCreateTime));
    }

    /**
     * 根据文章 ID 查询上一篇文章
     * @param articleId
     * @return
     */
    // TODO 查询上一篇就是id比这个小的
    default ArticleDO selectPreArticle(Long articleId){
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .orderByDesc(ArticleDO::getId) // 按文章 ID 降序排列
                .lt(ArticleDO::getId, articleId) // 查询比当前文章 ID 小的
                .last("limit 1")); // 第一条记录即为上一篇文章
    }

    /**
     * 根据文章 ID 查询下一篇文章
     * @param articleId
     * @return
     */
    default ArticleDO selectNextArticle(Long articleId){
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .orderByAsc(ArticleDO::getId) // 按文章 ID 升序排列
                .gt(ArticleDO::getId, articleId) // 查询比当前文章 ID 大的
                .last("limit 1")); // 第一条记录即为下一篇文章
    }
    /**
     * 阅读量+1
     * @param articleId
     * @return
     */
    default int increaseReadNum(Long articleId){
        return update(null,Wrappers.<ArticleDO>lambdaUpdate()
                .eq(ArticleDO::getId, articleId)
                .setSql("read_num = read_num + 1"));// TODO 这个为什么setSql
    }
    /**
     * 查询所有记录的阅读量
     * @return
     */
    default List<ArticleDO> selectAllReadNum(){
        return selectList(Wrappers.<ArticleDO>lambdaQuery()
                .select(ArticleDO::getReadNum));// TODO 这个为什么select
    }

    /**
     * 查询某天的文章发布数量
     * 按日分组，并统计每日发布的文章数量
     * @param startDate
     * @param endDate
     * @return
     */
    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count\n" +
            "FROM t_article\n" +
            "WHERE create_time >= #{startDate} AND create_time < #{endDate}\n" +
            "GROUP BY DATE(create_time)")
    List<ArticlePublishCountDO> selectDateArticlePublishCount(LocalDate startDate, LocalDate endDate);
    // TODO DATE(create_time)是计算日期的函数的吗
    /* select date (create_time) as date,count(*) count from t_article where create_time >=#{startDate} and create_time < #{endDate}
    group by date(create_time)*/
}