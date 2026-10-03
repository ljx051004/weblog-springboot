package com.xinyang.weblog.admin.service;

import com.xinyang.weblog.admin.model.vo.categpry.AddCategoryReqVO;
import com.xinyang.weblog.admin.model.vo.categpry.DeleteCategoryReqVO;
import com.xinyang.weblog.admin.model.vo.categpry.FindCategoryPageListReqVO;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;

public interface AdminCategoryService {
    /**
     * 添加分类
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    /**
     * 分页查询分类列表
     */
    PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO);

    /**
     * 删除分类
     */
    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    /**
     * 分类下拉列表数据获取
     */
    Response findCategorySelectList();

}
