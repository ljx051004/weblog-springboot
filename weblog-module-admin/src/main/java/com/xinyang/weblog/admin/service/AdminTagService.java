package com.xinyang.weblog.admin.service;

import com.xinyang.weblog.admin.model.vo.tag.AddTagReqVO;
import com.xinyang.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.xinyang.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.xinyang.weblog.admin.model.vo.tag.SearchTagReqVO;
import com.xinyang.weblog.common.utils.PageResponse;
import com.xinyang.weblog.common.utils.Response;

public interface AdminTagService {

    /**
     * 添加标签
     * @param addTagReqVO
     * @return
     */
    Response addTag(AddTagReqVO addTagReqVO);

    /**
     * 分页查询标签列表
     * @param findTagPageListReqVO
     * @return
     */
    PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO);

    /**
     * 删除标签
     * @param deleteTagReqVO
     * @return
     */
    Response deleteTag(DeleteTagReqVO deleteTagReqVO);


    /**
     * 根据标签名模糊查询标签下拉列表
     * @param searchTagReqVO
     * @return
     */
    Response searchTag(SearchTagReqVO searchTagReqVO);

}
