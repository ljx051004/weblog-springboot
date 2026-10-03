package com.xinyang.weblog.web.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyang.weblog.common.domain.dos.ArticleDO;
import com.xinyang.weblog.common.domain.dos.ArticleTagRelDO;
import com.xinyang.weblog.common.domain.dos.TagDO;
import com.xinyang.weblog.common.domain.mapper.ArticleMapper;
import com.xinyang.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.xinyang.weblog.common.domain.mapper.TagMapper;
import com.xinyang.weblog.common.enums.ResponseCodeEnum;
import com.xinyang.weblog.common.exception.BizException;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.convert.ArticleConvert;
import com.xinyang.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.xinyang.weblog.web.model.vo.tag.FindTagArticlePageListRspVO;
import com.xinyang.weblog.web.model.vo.tag.FindTagListRspVO;
import com.xinyang.weblog.web.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 前台获取标签列表
     * @return
     */
    @Override
    public Response findTagList() {
        // 获取所有标签
        List<TagDO> tagDOS = tagMapper.selectList(null);

        // 将DO转VO
        List<FindTagListRspVO> vos=null;
        if(!CollectionUtils.isEmpty(tagDOS)){
            vos=tagDOS.stream().map(
                    tagDO -> FindTagListRspVO.builder()
                            .id(tagDO.getId())
                            .name(tagDO.getName())
                            .build()
            ).collect(Collectors.toList());
        }
        return Response.success(vos);
    }

    /**
     * 前台获取标签下文章列表
     * @return
     */
    // TODO 博客前台:归档页、分类列表页、标签列表页这里面有三个分页查询 一定要弄清楚
    @Override
    public Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        // 获取分页参数
        Long current = findTagArticlePageListReqVO.getCurrent();
        Long size = findTagArticlePageListReqVO.getSize();
        // 获取标签ID
        Long tagId = findTagArticlePageListReqVO.getId();

        // 判断该标签是否存在
        TagDO tagDO = tagMapper.selectById(tagId);
        if(Objects.isNull(tagDO)){
            log.warn("==> 该标签不存在, tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }
        // 先根据标签ID查询出所有文章ID
        // 这一步先得到文章ID列表
        // id          关联记录自增主键
        // article_id  文章 ID
        // tag_id      标签 ID
        List<ArticleTagRelDO> articleTagRelDOS=articleTagRelMapper.selectByTagId(tagId);

        // 判断该标签下是否有文章
        if(CollectionUtils.isEmpty(articleTagRelDOS)){
            log.info("==> 该标签下还未发布任何文章, tagId: {}", tagId);
            return PageResponse.success(null, null);
        }
        // 得到文章的ID
        List<Long> articleIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getArticleId).collect(Collectors.toList());

        // 根据文章ID集合来查询文章分页数据
        Page<ArticleDO> page =articleMapper.selectPageListByArticleIds(articleIds, current, size);
        List<ArticleDO> articleDOS = page.getRecords();

        // 将DO转VO
        List<FindTagArticlePageListRspVO> vos=null;
        if(!CollectionUtils.isEmpty(articleDOS)){
            vos=articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2TagArticleVO(articleDO))
                    .collect(Collectors.toList());
        }
        return PageResponse.success(page, vos);
    }
}
