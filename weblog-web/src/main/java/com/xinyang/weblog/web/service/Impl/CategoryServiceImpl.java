package com.xinyang.weblog.web.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyang.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.xinyang.weblog.common.domain.dos.ArticleDO;
import com.xinyang.weblog.common.domain.dos.CategoryDO;
import com.xinyang.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.xinyang.weblog.common.domain.mapper.ArticleMapper;
import com.xinyang.weblog.common.domain.mapper.CategoryMapper;
import com.xinyang.weblog.common.enums.ResponseCodeEnum;
import com.xinyang.weblog.common.exception.BizException;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.web.convert.ArticleConvert;
import com.xinyang.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.xinyang.weblog.web.model.vo.category.FindCategoryArticlePageListRspVO;
import com.xinyang.weblog.web.model.vo.category.FindCategoryListRspVO;
import com.xinyang.weblog.web.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ArticleMapper articleMapper;


    /**
     * 前台获取分类列表
     * @return
     */
    @Override
    public Response findCategoryList() {
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);

        // 将DO转成VO
        List<FindCategoryListRspVO> vos=null;
        if(!CollectionUtils.isEmpty(categoryDOS)){
            vos=categoryDOS.stream().map(
                    categoryDO -> FindCategoryListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .build())
            .collect(Collectors.toList());
        }
        return Response.success(vos);
    }

    /**
     * 前台获取分类下文章分页列表
     * @param findCategoryArticlePageListReqVO
     * @return
     */
    // TODO 博客前台:归档页、分类列表页、标签列表页这里面有三个分页查询 一定要弄清楚
    @Override
    public Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO) {
        // 获取分页参数
        Long current = findCategoryArticlePageListReqVO.getCurrent();
        Long size = findCategoryArticlePageListReqVO.getSize();
        Long categoryId = findCategoryArticlePageListReqVO.getId();

        // 根据分类ID查询分类
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);

        // 判断该分类是否存在
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 该分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 先查询该分类下所有关联的文章ID
        List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectListByCategoryId(categoryId);
        // 若该分类下未发布任何文章，则返回空分页
        if (CollectionUtils.isEmpty(articleCategoryRelDOS)) {
            log.info("==> 该分类下还未发布任何文章, categoryId: {}", categoryId);
            return PageResponse.success(null, null);
        }

        List<Long> articleIds = articleCategoryRelDOS.stream().map(ArticleCategoryRelDO::getArticleId).collect(Collectors.toList());

        // 根据文章ID集合来查询分页数据
        Page<ArticleDO> page = articleMapper.selectPageListByArticleIds(articleIds, current, size);
        List<ArticleDO> articleDOS = page.getRecords();

        // 将DO转成VO
        List<FindCategoryArticlePageListRspVO> vos=null;
        if(!CollectionUtils.isEmpty(articleDOS)){
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2CategoryArticleVO(articleDO))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }
}
