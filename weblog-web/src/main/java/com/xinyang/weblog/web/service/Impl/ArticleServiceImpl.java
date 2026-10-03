package com.xinyang.weblog.web.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xinyang.weblog.admin.event.ReadArticleEvent;
import com.xinyang.weblog.common.domain.dos.*;
import com.xinyang.weblog.common.domain.mapper.*;
import com.xinyang.weblog.common.enums.ResponseCodeEnum;
import com.xinyang.weblog.common.exception.BizException;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.convert.ArticleConvert;
import com.xinyang.weblog.web.markdown.MarkdownHelper;
import com.xinyang.weblog.web.model.vo.article.*;
import com.xinyang.weblog.web.model.vo.category.FindCategoryListRspVO;
import com.xinyang.weblog.web.model.vo.tag.FindTagListRspVO;
import com.xinyang.weblog.web.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;// TODO ApplicationEventPublisher这个怎么来的

    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        Long current = findIndexArticlePageListReqVO.getCurrent();
        Long size = findIndexArticlePageListReqVO.getSize();

        // 第一步：分页查询文章主体记录
        Page<ArticleDO> articleDOPage = articleMapper.selectArticleList(current, size, null, null, null);

        // 返回的分页数据
        List<ArticleDO> articleDOS  = articleDOPage.getRecords();

        // 将文章DO转成VO
        List<FindIndexArticlePageListRspVO> vos=null;
        if(!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2VO(articleDO))
                    .collect(Collectors.toList());
            // 拿到所有文章的 ID 集合
            // todo 为什么要拿到所有文章的 ID 集合？
            List<Long> articleIds = articleDOS.stream().map(ArticleDO::getId).collect(Collectors.toList());

            // 第二步：设置文章所属分类
            // 查询所有分类
            List<CategoryDO> categoryDOS = categoryMapper.selectList(null);
            // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
            // todo 这个转成Map需要弄清楚
            Map<Long, String> categoryIdNameMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));

            // 根据文章 ID 批量查询所有关联记录
            List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(articleIds);
            // todo 完全不知道啥意思
            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章对应的关联数据
                Optional<ArticleCategoryRelDO> optional = articleCategoryRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).findAny();

                // 若不为空
                if (optional.isPresent()) {
                    ArticleCategoryRelDO articleCategoryRelDO = optional.get();
                    Long categoryId = articleCategoryRelDO.getCategoryId();
                    // 通过分类 ID 从 map 中拿到对应的分类名称
                    String categoryName = categoryIdNameMap.get(categoryId);

                    FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                            .id(categoryId)
                            .name(categoryName)
                            .build();
                    // 设置到当前 vo 类中
                    vo.setCategory(findCategoryListRspVO);
                }
            });
            // 第三步：设置文章标签
            // 查询所有标签
            List<TagDO> tagDOS = tagMapper.selectList(null);
            // 转 Map, 方便后续根据标签 ID 拿到对应的标签名称
            Map<Long, String> mapIdNameMap = tagDOS.stream().collect(Collectors.toMap(TagDO::getId, TagDO::getName));

            // 拿到所有文章的标签关联记录
            List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleIds(articleIds);

            // todo 这个搞清楚
            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章的标签关联记录
                List<ArticleTagRelDO> articleTagRelDOList = articleTagRelDOS.stream().filter(
                        rel -> Objects.equals(rel.getArticleId(), currArticleId)).collect(Collectors.toList());
                List<FindTagListRspVO> findTagListRspVOS = Lists.newArrayList();
                // 将关联记录 DO 转 VO, 并设置对应的标签名称
                articleTagRelDOList.forEach(articleTagRelDO -> {
                    Long tagId = articleTagRelDO.getTagId();
                    String tagName = mapIdNameMap.get(tagId);

                    FindTagListRspVO findTagListRspVO = FindTagListRspVO.builder()
                            .id(tagId)
                            .name(tagName)
                            .build();
                    findTagListRspVOS.add(findTagListRspVO);
                });
                // 设置转换后的标签数据
                vo.setTags(findTagListRspVOS);
            });
        }
            return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        // 获取文章ID
        Long articleId = findArticleDetailReqVO.getArticleId();
        // 根据文章ID获取文章
        ArticleDO articleDO = articleMapper.selectById(articleId);
        // 判断文章是否存在
        if (Objects.isNull(articleDO)) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 查询正文
        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        // DO 转 VO
        FindArticleDetailRspVO vo = FindArticleDetailRspVO.builder()
                .title(articleDO.getTitle())
                .createTime(articleDO.getCreateTime())
                .content(MarkdownHelper.convertMarkdown2Html(articleContentDO.getContent()))
                .readNum(articleDO.getReadNum())
                .build();

        // 根据文章ID获取文章关联的分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);
        // 根据文章关联分类获取分类
        Long categoryId = articleCategoryRelDO.getCategoryId();
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        // DO转VO
        vo.setCategoryId(categoryDO.getId());
        vo.setCategoryName(categoryDO.getName());

        // 根据文章ID获取文章关联的标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        // 根据文章关联分类获取分类
        List<Long> tagIds  = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());
        List<TagDO> tagDOS = tagMapper.selectByIds(tagIds);

        // 标签 DO 转 VO TODO 因为这个出参要的是tag的集合 所以返回这个
        List<FindTagListRspVO> tagVOS = tagDOS.stream()
                .map(tagDO -> FindTagListRspVO.builder().id(tagDO.getId()).name(tagDO.getName()).build())
                .collect(Collectors.toList());
        vo.setTags(tagVOS);

        // 上一篇
        ArticleDO preArticleDO = articleMapper.selectPreArticle(articleId);
        // 判断上一篇是否存在
        if(Objects.nonNull(preArticleDO)){
            // DO 转 VO
            FindPreNextArticleRspVO preArticleVO=FindPreNextArticleRspVO.builder()
                    .articleId(preArticleDO.getId())
                    .articleTitle(preArticleDO.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 下一篇
        ArticleDO nextArticleDO = articleMapper.selectNextArticle(articleId);
        // 判断下一篇是否存在
        if(Objects.nonNull(nextArticleDO)){
            // DO 转 VO
            FindPreNextArticleRspVO nextArticleVO=FindPreNextArticleRspVO.builder()
                    .articleId(nextArticleDO.getId())
                    .articleTitle(nextArticleDO.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        // todo 这个添加自线程问题
        //  发布文章阅读事件
        eventPublisher.publishEvent(new ReadArticleEvent(this, articleId));

        return Response.success(vo);
    }
}
