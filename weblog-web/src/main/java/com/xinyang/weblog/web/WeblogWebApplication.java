package com.xinyang.weblog.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello world!
 *
 */
@EnableScheduling // 启用定时任务
@ComponentScan(basePackages = "com.xinyang.weblog.*")//多模块项目中，必需手动指定扫描 com.xinyang.weblog 包下面的所有类
@SpringBootApplication
public class WeblogWebApplication {
    public static void main( String[] args ){
        SpringApplication.run(WeblogWebApplication.class, args);
    }
}
