package com.xinyang.weblog.jwt.service;

import com.xinyang.weblog.common.domain.dos.UserDO;
import com.xinyang.weblog.common.domain.dos.UserRoleDO;
import com.xinyang.weblog.common.domain.mapper.UserMapper;
import com.xinyang.weblog.common.domain.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户
        UserDO userDo = userMapper.findByUsername(username);

        // 判断用户是否存在
        if(Objects.isNull(userDo)){
            throw new UsernameNotFoundException("该用户不存在");
        }

        // 用户角色
        List<UserRoleDO> roleDOS = userRoleMapper.selectByUsername(username);

        String[] roleArr = new String[0];


        //转数组
        // 这里将admin test两个用户的角色放到数组中了
        if(!CollectionUtils.isEmpty(roleDOS)){
            List<String> roles = roleDOS.stream().map(p -> p.getRole()).collect(Collectors.toList());
            roleArr = roles.toArray(new String[roles.size()]);
        }

        // authorities 用于指定角色，这里写死为 ADMIN 管理员
        return User.withUsername(userDo.getUsername())
                .password(userDo.getPassword())
                .authorities(roleArr)
                .build();
    }
}
