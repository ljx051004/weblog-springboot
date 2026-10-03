package com.xinyang.weblog.admin.controller;

import com.xinyang.weblog.admin.model.vo.categpry.DeleteCategoryReqVO;
import com.xinyang.weblog.admin.model.vo.tag.AddTagReqVO;
import com.xinyang.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.xinyang.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.xinyang.weblog.admin.model.vo.tag.SearchTagReqVO;
import com.xinyang.weblog.admin.service.AdminTagService;
import com.xinyang.weblog.common.aspect.ApiOperationLog;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/admin"))
@Api(tags="Admin 标签模块")
public class AdminTagController {

    @Autowired
    private AdminTagService adminTagService;

    /**
     * 添加标签
     * @param addTagReqVO
     * @return
     */
    @PostMapping("/tag/add")
    @ApiOperation(value = "添加标签")
    @ApiOperationLog(description = "添加标签")
    public Response addTag(@RequestBody AddTagReqVO addTagReqVO) {
        return adminTagService.addTag(addTagReqVO);
    }
    /**
     * 添加分页接口查询
     */
    @PostMapping("/tag/list")
    @ApiOperation(value = "标签分页数据获取")
    @ApiOperationLog(description = "标签分页数据获取")
    public PageResponse findTagList(@RequestBody @Validated FindTagPageListReqVO findTagPageListReqVO){
        return adminTagService.findTagList(findTagPageListReqVO);
    }
    /**
     * 删除标签
     */
    @PostMapping("/tag/delete")
    @ApiOperation(value = "删除标签")
    @ApiOperationLog(description = "删除标签")
    public Response deleteTag(@RequestBody @Validated DeleteTagReqVO deleteTagReqVO){
        return adminTagService.deleteTag(deleteTagReqVO);
    }
    /**
     * 标签模糊查询
     */
    @PostMapping("/tag/search")
    @ApiOperation(value = "标签模糊查询")
    @ApiOperationLog(description = "标签模糊查询")
    public Response searchTag(@RequestBody @Validated SearchTagReqVO searchTagReqVO){
        return adminTagService.searchTag(searchTagReqVO);
    }
}
