package com.xinyang.weblog.jwt;

import com.xinyang.weblog.common.domain.dos.UserDO;
import com.xinyang.weblog.common.domain.mapper.UserMapper;
import com.xinyang.weblog.web.WeblogWebApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest(classes = WeblogWebApplication.class)
@Slf4j
public class WeblogWebApplicationTests{

    private static final Logger log = LoggerFactory.getLogger(WeblogWebApplicationTests.class);

    @Test
    public void test(){
        log.info("测试");
    }

    @Test
    public void testLog(){
        log.info("这是一行 Info 级别日志");
        log.warn("这是一行 Warn 级别日志");
        log.error("这是一行 Error 级别日志");

        String author="ljx";
        log.info("这是一行带有占位符日志，作者：{}", author);
    }
    @Autowired
    private UserMapper userMapper;
    @Test
    public void insert(){
        UserDO userDO = UserDO.builder()
                .username("朱欣语")
                .password("12345")
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(false)
                .build();
        userMapper.insert(userDO);
    }
}
