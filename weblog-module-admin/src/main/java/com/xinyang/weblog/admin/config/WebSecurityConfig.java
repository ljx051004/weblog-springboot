package com.xinyang.weblog.admin.config;

import com.xinyang.weblog.jwt.config.JwtAuthenticationSecurityConfig;
import com.xinyang.weblog.jwt.filter.TokenAuthenticationFilter;
import com.xinyang.weblog.jwt.handler.RestAccessDeniedHandler;
import com.xinyang.weblog.jwt.handler.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * Spring Security 配置类
 * 负责安全规则配置（URL 权限、会话策略、异常处理等
 */
@Configuration
@EnableWebSecurity //开启Spring Security的自定义配置
//用于启用方法级别的安全性设置
//prePostEnabled = true 表示启用 @PreAuthorize 和 @PostAuthorize 注解，
//securedEnabled = true 表示启用 @Secured 注解
//可以在方法级别使用这些注解来定义访问控制规则
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(). // 禁用 csrf
                formLogin().disable() // 禁用表单登录
                .apply(jwtAuthenticationSecurityConfig) // 设置用户登录认证相关配置
                .and()
                .authorizeHttpRequests()
                .mvcMatchers("/admin/**").authenticated() // 认证所有以 /admin 为前缀的 URL 资源
                .anyRequest().permitAll() // 其他都需要放行，无需认证
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) ;// 前后端分离，无需创建会话
                /*.and()
                .httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint) // 处理用户未登录访问受保护的资源的情况
                .and()
                .exceptionHandling().accessDeniedHandler(restAccessDeniedHandler) // 处理登录成功受保护的资源，但权限不足
                .and()

                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;*/
        http.exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // 处理用户未登录访问受保护的资源的情况
                .accessDeniedHandler(restAccessDeniedHandler) // 处理登录成功访问受保护的资源，但权限不足
                .and()
                // 将Token校验过滤器放到UsernamePasswordAuthenticationFilter之前执行
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


    }
    /**
     * Token 校验过滤器
     * @return
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }
}
