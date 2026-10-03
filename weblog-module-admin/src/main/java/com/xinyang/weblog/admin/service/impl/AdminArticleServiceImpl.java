package com.xinyang.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xinyang.weblog.admin.convert.ArticleDetailConvert;
import com.xinyang.weblog.admin.model.vo.article.*;
import com.xinyang.weblog.admin.service.AdminArticleService;
import com.xinyang.weblog.common.domain.dos.*;
import com.xinyang.weblog.common.domain.mapper.*;
import com.xinyang.weblog.common.enums.ResponseCodeEnum;
import com.xinyang.weblog.common.exception.BizException;
import com.xinyang.weblog.common.model.vo.SelectRspVO;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminArticleServiceImpl implements AdminArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    /**
     * 发布文章
     * @param publishArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        // 将VO转成ArticleDO
        ArticleDO articleDO=ArticleDO.builder()
                .title(publishArticleReqVO.getTitle())
                .cover(publishArticleReqVO.getCover())
                .summary(publishArticleReqVO.getSummary())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.insert(articleDO);
        // 拿到插入数据的ID
        Long articleId  = articleDO.getId();

        // 将VO转成ArticleContentDO
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(publishArticleReqVO.getContent())
                .build();
        articleContentMapper.insert(articleContentDO);

        // 处理文章关联的分类
        Long categoryId = publishArticleReqVO.getCategoryId();

        // 判断分类是否存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if(Objects.isNull(categoryDO)){
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }
        // 将categoryId保存到ArticleCategoryRelDO
        ArticleCategoryRelDO articleCategoryRelDO=ArticleCategoryRelDO.builder()
                .categoryId(categoryId)
                .articleId(articleId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 保存文章关联的标签集合
        List<String>  publishTags = publishArticleReqVO.getTags();
        insertTags(articleId,publishTags);
        return Response.success();
    }

    /**
     * 保存标签
     * @param articleId
     * @param publishTags
     */
    //TODO
    private void insertTags(Long articleId,List<String> publishTags) {
        // 筛选提交的标签（表中不存在的标签）
        List<String> notExistTags = null;
        // 筛选提交的标签（表中已存在的标签）
        List<String> existedTags = null;

        // 查询所有标签
        List<TagDO> tagDOS = tagMapper.selectList(null);

        // 如果表中还没有添加任何标签
        if (CollectionUtils.isEmpty(tagDOS)) {
            notExistTags = publishTags;
        } else {
            // 获取表中的标签ID
            List<String> tagIds = tagDOS.stream().map(tagDO -> String.valueOf(tagDO.getId())).collect(Collectors.toList());

            // 表中已添加相关标签，则需要筛选
            // 通过标签 ID 来筛选，包含对应 ID 则表示提交的标签是表中存在的
            existedTags = publishTags.stream().filter(publishTag -> tagIds.contains(publishTag)).collect(Collectors.toList());
            // 否则则是不存在的
            notExistTags = publishTags.stream().filter(publishTag -> !tagIds.contains(publishTag)).collect(Collectors.toList());

            // 补充逻辑：
            // 还有一种可能：按字符串名称提交上来的标签，也有可能是表中已存在的，比如表中已经有了 Java 标签，用户提交了个 java 小写的标签，需要内部装换为 Java 标签
            // todo 这里根本不明白 回来要明白
            Map<String, Long> tagNameIdMap = tagDOS.stream().collect(Collectors.toMap(tagDO -> tagDO.getName().toLowerCase(), TagDO::getId));

            // 使用迭代器进行安全的删除操作
            // todo 完全没懂
            Iterator<String> iterator = notExistTags.iterator();
            while (iterator.hasNext()) {
                String notExistTag = iterator.next();
                // 转小写, 若 Map 中相同的 key，则表示该新标签是重复标签
                if (tagNameIdMap.containsKey(notExistTag.toLowerCase())) {
                    // 从不存在的标签集合中清除
                    iterator.remove();
                    // 并将对应的 ID 添加到已存在的标签集合
                    existedTags.add(String.valueOf(tagNameIdMap.get(notExistTag.toLowerCase())));
                }
            }
        }
        // 将提交的上来的，已存在于表中的标签，文章-标签关联关系入库
        if (!CollectionUtils.isEmpty(existedTags)) {
            ArrayList<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            // 遍历已存在的标签集合
            existedTags.forEach(tagId -> {
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(Long.valueOf(tagId))
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
        // 将提交的上来的，不存在于表中的标签，入库保存
        if (!CollectionUtils.isEmpty(notExistTags)) {
            // 需要先将标签入库，拿到对应标签 ID 后，再把文章-标签关联关系入库
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            notExistTags.forEach(tagName -> {
                TagDO tagDO = TagDO.builder()
                        .name(tagName)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();

                tagMapper.insert(tagDO);
                // 拿到保存的标签 ID
                Long tagId = tagDO.getId();
                // 文章-标签关联关系
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
    }
    /**
     * 删除文章
     * @param deleteArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId  = deleteArticleReqVO.getId();

        // 1. 删除文章
        articleMapper.deleteById(articleId);
        // 2. 删除文章内容
        articleContentMapper.deleteByArticleId(articleId);
        // 3. 删除文章标签关联关系
        articleTagRelMapper.deleteByArticleId(articleId);
        // 4. 删除文章分类关联关系
        articleCategoryRelMapper.deleteByArticleId(articleId);

        return Response.success();
    }

    /**
     * 分页查询文章列表
     * @param findArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArticleList(FindArticlePageListReqVO findArticlePageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long size = findArticlePageListReqVO.getSize();
        Long current = findArticlePageListReqVO.getCurrent();
        String title = findArticlePageListReqVO.getTitle();
        LocalDate startDate = findArticlePageListReqVO.getStartDate();
        LocalDate endDate = findArticlePageListReqVO.getEndDate();

        // 执行分页查询
        Page<ArticleDO> articleDOPage = articleMapper.selectArticleList(current,size,title,startDate,endDate);

        List<ArticleDO> articleDOS  = articleDOPage.getRecords();

        // 将DO转成VO
        List<FindArticlePageListRspVO> vos=null;
        if(!CollectionUtils.isEmpty(articleDOS)){
            vos=articleDOS.stream().map(
                    articleDO -> FindArticlePageListRspVO.builder()
                            .id(articleDO.getId())
                            .title(articleDO.getTitle())
                            .cover(articleDO.getCover())
                            .createTime(articleDO.getCreateTime())
                            .build()
            ).collect(Collectors.toList());
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
        // 得到文章Id
        Long articleId = findArticleDetailReqVO.getId();

        ArticleDO articleDO = articleMapper.selectById(articleId);
        // 判断文章是否存在
        if(Objects.isNull(articleDO)){
            log.warn("==> 查询的文章不存在，articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }
        // 得到id对应的内容
        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        // 得到id对应的分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);

        // 获取对应标签 ID 集合
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());
        // 将DO转VO
        /*FindArticleDetailRspVO findArticleDetailRspVO=FindArticleDetailRspVO.builder()
                .content(articleContentDO.getContent())
                .categoryId(articleCategoryRelDO.getCategoryId())
                .tagIds(tagIds)
                .build();*/
        // 用实例化的方法转成VO
        FindArticleDetailRspVO vo = ArticleDetailConvert.INSTANCE.convertDO2VO(articleDO);
        vo.setContent(articleContentDO.getContent());
        vo.setCategoryId(articleCategoryRelDO.getCategoryId());
        vo.setTagIds(tagIds);
        return Response.success(vo);
    }

    /**
     * 更新文章
     * @param updateArticleReqVO
     * @return
     */
    @Override
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        // 查询文章Id
        Long articleId = updateArticleReqVO.getId();

        // 因为ArticleDO中有title，cover,summary 字段
        // 所以可以使用ArticleDO来接收,并更新
        ArticleDO articleDO=ArticleDO.builder()
                .id(articleId)
                .title(updateArticleReqVO.getTitle())
                .cover(updateArticleReqVO.getCover())
                .summary(updateArticleReqVO.getSummary())
                .updateTime(LocalDateTime.now())
                .build();
        int count = articleMapper.updateById(articleDO);
        // 判断能否更新成功
        if(count==0){
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 现在还差content,categoryId,tags
        // 先将VO转成ArticleContentDO，并保存
        ArticleContentDO articleContentDO=ArticleContentDO.builder()
                .articleId(articleId)
                .content(updateArticleReqVO.getContent())
                .build();
        // 更新文章内容,这里只有 articleId 字段 ,所有用mybatis-plus自定义mapper方法
        articleContentMapper.updateByArticleId(articleContentDO);

        // 这里不能直接像上面更新内容一样更新文章
        // 因为是多对多的关系 要先 DELETE 再 INSERT
        //  更新文章分类
        Long categoryId = updateArticleReqVO.getCategoryId();

        // 校验文章分类是否存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 先删除原本存在的文章分类，然后在保存更新的
        articleCategoryRelMapper.deleteByArticleId(articleId);

        ArticleCategoryRelDO articleCategoryRelDO=ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 更新文章标签
        // 先删除原本存在的文章标签，然后在保存更新的
        articleTagRelMapper.deleteByArticleId(articleId);
        // todo 这里保存标签回来还得搞清楚
        List<String> publishTags = updateArticleReqVO.getTags();
        insertTags( articleId,publishTags);

        return Response.success();
    }

    /**
     * 获取标签下拉列表
     * @return
     */
    @Override
    public Response findTagSelectList() {
        // 查询所有标签，Wrappers.emptyWrapper() 表示查询条件为空
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // 将DO转VO
        List<SelectRspVO> vos=null;
        if(!CollectionUtils.isEmpty(tagDOS)){
            vos=tagDOS.stream().map(
                    tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build()
            ).collect(Collectors.toList());
        }

        return Response.success(vos);
    }
}
