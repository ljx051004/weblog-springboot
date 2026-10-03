package com.xinyang.weblog.admin.controller;

import com.xinyang.weblog.admin.model.vo.categpry.AddCategoryReqVO;
import com.xinyang.weblog.admin.model.vo.categpry.DeleteCategoryReqVO;
import com.xinyang.weblog.admin.model.vo.categpry.FindCategoryPageListReqVO;
import com.xinyang.weblog.admin.service.AdminCategoryService;
import com.xinyang.weblog.common.aspect.ApiOperationLog;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/admin"))
@Api(tags="Admin 分类模块")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    /**
     * 添加分类
     */
    @PostMapping("/category/add")
    @ApiOperation(value = "添加分类")
    @ApiOperationLog(description = "添加分类")
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO){
        return adminCategoryService.addCategory(addCategoryReqVO);
    }

    /**
     * 添加分页接口查询
     */
    @PostMapping("/category/list")
    @ApiOperation(value = "分类分页数据获取")
    @ApiOperationLog(description = "分类分页数据获取")
    public PageResponse findCategoryList(@RequestBody @Validated FindCategoryPageListReqVO findCategoryPageListReqVO){
        return adminCategoryService.findCategoryList(findCategoryPageListReqVO);
    }
    /**
     * 删除分类
     */
    @PostMapping("/category/delete")
    @ApiOperation(value = "删除分类")
    @ApiOperationLog(description = "删除分类")
    public Response deleteCategory(@RequestBody @Validated DeleteCategoryReqVO deleteCategoryReqVO){
        return adminCategoryService.deleteCategory(deleteCategoryReqVO);
    }

    /**
     * 分类下拉列表数据获取
     */
    @PostMapping("/category/select/list")
    @ApiOperation(value = "分类 Select 下拉列表数据获取")
    @ApiOperationLog(description = "分类 Select 下拉列表数据获取")
    public Response findCategorySelectList(){
        return adminCategoryService.findCategorySelectList();
    }

}
