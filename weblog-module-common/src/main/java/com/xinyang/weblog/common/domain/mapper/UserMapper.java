package com.xinyang.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xinyang.weblog.common.domain.dos.UserDO;

import java.time.LocalDateTime;

public interface UserMapper extends BaseMapper<UserDO> {
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    default UserDO findByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername,username);
        return selectOne(wrapper);
    }
    /**
     * 根据用户名更新密码
     * @param username
     * @param password
     * @return
     */
    default int updatePasswordByUsername(String username,String password){
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        //update user set user.password = ?, user.update_time = ? where user.username = ?
        //更新数据
        updateWrapper.set(UserDO::getPassword, password);
        updateWrapper.set(UserDO::getUpdateTime, LocalDateTime.now());

        //更新条件
        updateWrapper.eq(UserDO::getUsername, username);
        return update(null, updateWrapper);
    }
}
