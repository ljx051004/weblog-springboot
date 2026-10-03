package com.xinyang.weblog.common.exception;

/**
 * @author: 信阳
 * @url: www.xinyang.com
 * @date: 2023-08-15 9:54
 * @description: 通用异常接口
 **/
public interface BaseExceptionInterface {
    // 获取错误码
    String getErrorCode();

    // 获取错误信息
    String getErrorMessage();
}
