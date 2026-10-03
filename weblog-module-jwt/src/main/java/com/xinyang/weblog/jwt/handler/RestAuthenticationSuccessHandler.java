package com.xinyang.weblog.jwt.handler;

import com.xinyang.weblog.common.utils.Response;
import com.xinyang.weblog.jwt.utils.JwtTokenHelper;
import com.xinyang.weblog.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
@Slf4j
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 用UserDetails获取用户名
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 根据用户名获取token
        String username = userDetails.getUsername();
        String token = jwtTokenHelper.generateToken(username);

        ResultUtil.ok(response, Response.success(token));
    }
}
