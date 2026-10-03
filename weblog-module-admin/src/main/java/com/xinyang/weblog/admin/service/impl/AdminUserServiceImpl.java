package com.xinyang.weblog.admin.service.impl;

import com.xinyang.weblog.admin.model.vo.user.FindUserInfoRspVO;
import com.xinyang.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.xinyang.weblog.admin.service.AdminUserService;
import com.xinyang.weblog.common.domain.mapper.UserMapper;
import com.xinyang.weblog.common.enums.ResponseCodeEnum;
import com.xinyang.weblog.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;// 密码加密器

    /**
     * 修改密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    @Override
    public Response updatePasswordByUsername(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        // 得到用户名和密码
        String username = updateAdminUserPasswordReqVO.getUsername();
        String password = updateAdminUserPasswordReqVO.getPassword();

        // 把密码加密
        String encodePassword = passwordEncoder.encode(password);

        // 把数据更新到数据库
        int count = userMapper.updatePasswordByUsername(username, encodePassword);

        // 判断密码是否修改成功
        if(count !=1){
            return Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }
        return Response.success();
    }

    /**
     * 用来获取用户信息
     * @return
     */
    @Override
    public Response findUserInfo() {
        // 获取ThreadLocal中的用户信息
        // TODO 这里回来要搞清楚Authentication
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String username = authentication.getName();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // 封装返回值
        // return Response.success(FindUserInfoRspVO.builder().username(username).build());
        FindUserInfoRspVO vo = FindUserInfoRspVO.builder()
                .username(username)
                .build();
        return Response.success(vo);

    }
}
