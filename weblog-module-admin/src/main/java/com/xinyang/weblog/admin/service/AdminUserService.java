package com.xinyang.weblog.admin.service;

import com.xinyang.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.xinyang.weblog.common.utils.Response;

public interface AdminUserService {
    /**
     * 修改密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response updatePasswordByUsername(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);

    /**
     * 获取用户信息
     * @return
     */
    Response findUserInfo();

}
