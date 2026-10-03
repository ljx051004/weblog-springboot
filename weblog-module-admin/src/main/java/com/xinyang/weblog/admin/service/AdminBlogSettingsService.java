package com.xinyang.weblog.admin.service;

import com.xinyang.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.xinyang.weblog.common.utils.Response;

public interface AdminBlogSettingsService {
    /**
     * 更新博客设置信息
     * @param updateBlogSettingsReqVO
     * @return
     */
    Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO);

    Response findDetail();
}
