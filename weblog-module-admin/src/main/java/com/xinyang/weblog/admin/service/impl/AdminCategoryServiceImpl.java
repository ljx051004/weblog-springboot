package com.xinyang.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyang.weblog.admin.model.vo.categpry.AddCategoryReqVO;
import com.xinyang.weblog.admin.model.vo.categpry.DeleteCategoryReqVO;
import com.xinyang.weblog.admin.model.vo.categpry.FindCategoryPageListReqVO;
import com.xinyang.weblog.admin.model.vo.categpry.FindCategoryPageListRspVO;
import com.xinyang.weblog.admin.service.AdminCategoryService;
import com.xinyang.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.xinyang.weblog.common.domain.dos.CategoryDO;
import com.xinyang.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.xinyang.weblog.common.domain.mapper.CategoryMapper;
import com.xinyang.weblog.common.enums.ResponseCodeEnum;
import com.xinyang.weblog.common.exception.BizException;
import com.xinyang.weblog.common.model.vo.SelectRspVO;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    /**
     * 添加分类
     *
     * @param addCategoryReqVO
     * @return
     */
    @Override
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        // 先得到分类的名称
        String categoryName = addCategoryReqVO.getName();
        // 判断是否存在这个分类
        CategoryDO categoryDO = categoryMapper.selectByName(categoryName);
        if (Objects.nonNull(categoryDO)) {
            log.warn("分类名称： {}, 此分类已存在", categoryName);
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }
        // 不存在添加这个分类
        //  构建DO
        //  VO → DO：前端传来的 VO 转成 DO，用于写入数据库
        //  DO → VO：从数据库查出的 DO 转成 VO，用于返回给前端
        CategoryDO insertCategoryDO = CategoryDO.builder()
                .name(categoryName.trim())
                .build();
        categoryMapper.insert(insertCategoryDO);
        return Response.success();
    }

    /**
     * 分页查询分类列表
     */
    // todo 回来好好看一眼mybatis-plus的分页插件
    @Override
    public PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long size = findCategoryPageListReqVO.getSize();
        Long current = findCategoryPageListReqVO.getCurrent();
        String name = findCategoryPageListReqVO.getName();
        LocalDate startDate = findCategoryPageListReqVO.getStartDate();
        LocalDate endDate = findCategoryPageListReqVO.getEndDate();

        Page<CategoryDO> categoryDOPage = categoryMapper.selectPageList(current, size, name, startDate, endDate);

        List<CategoryDO> records = categoryDOPage.getRecords();// 获取分页数据

        // DO转VO返回
        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .createTime(categoryDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }
        return PageResponse.success(categoryDOPage, vos);
    }

    /**
     * 删除分类
     */
    // todo 根据用户要删除分类，首先先判断该分类下是否已经有文章，若有，则提示需要先删除分类下所有文章，才能删除
    @Override
    public Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        // 分类Id
        Long categoryId = deleteCategoryReqVO.getId();

        // 校验该分类下是否已经有文章，若有，则提示需要先删除分类下所有文章，才能删除
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectOneByCategoryId(categoryId);
        // 判断是否已经有文章
        if(Objects.isNull(articleCategoryRelDO)){
            log.warn("==> 此分类下包含文章，无法删除，categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_CAN_NOT_DELETE);
        }

        // 删除分类
        categoryMapper.deleteById(categoryId);
        return Response.success();
    }

    /**
     * 分类下拉列表数据获取
     */
    @Override
    public Response findCategorySelectList() {
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);
        // 将DO转成VO
        List<SelectRspVO> selectRspVOS = new ArrayList<>();
        // 判断是否为空
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            selectRspVOS = categoryDOS.stream()
                    .map(categoryDO -> SelectRspVO.builder()
                            .label(categoryDO.getName())
                            .value(categoryDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }
        return Response.success(selectRspVOS);
    }
}
