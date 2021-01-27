package com.aqi.configer.exception;

import lombok.Data;

/**
 * 响应数据(结果)最外层对象
 * @author lucong
 * @date 2020/01/23
 */
@Data
public class ResultVo<T> {

    //状态说明
    private String state;
    // 状态码
    private Integer code;
    // 提示信息
    private String msg;
    // 响应数据
    private T data;
    //判断是否登录
    private boolean isLogin;
}
