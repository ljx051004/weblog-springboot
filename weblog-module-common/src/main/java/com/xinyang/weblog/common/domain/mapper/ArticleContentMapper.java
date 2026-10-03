package com.xinyang.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xinyang.weblog.common.domain.dos.ArticleContentDO;

public interface ArticleContentMapper extends BaseMapper<ArticleContentDO> {
    /**
     * 根据文章 ID 删除记录
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId){
        // todo
        // Wrappers.<ArticleContentDO>lambdaQuery()拿出一个空的 WHERE 板子
        return delete(Wrappers.<ArticleContentDO>lambdaQuery()
                .eq(ArticleContentDO::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 查询记录
     * @param articleId
     * @return
     */
    default ArticleContentDO  selectByArticleId(Long articleId){
        return selectOne(Wrappers.<ArticleContentDO>lambdaQuery()
                .eq(ArticleContentDO::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 更新记录
     * @param articleContentDO
     * @return
     */
    default int updateByArticleId(ArticleContentDO articleContentDO){
        // todo update(要修改的数据(就是set部分的内容),剩下这全部都是where部分的内容)
        // (ArticleContentDO::getArticleId, articleContentDO.getArticleId()))
        // (指定用 article_id 列作为条件字段(article_id),取该对象中的 articleId 值作为条件值(articleId))
        // 相当于 article_id = (articleId)
        return update(articleContentDO,Wrappers.<ArticleContentDO>lambdaUpdate()
                .eq(ArticleContentDO::getArticleId, articleContentDO.getArticleId()));
    }
}
