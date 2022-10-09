package com.mobei.register.server.web;

import lombok.Data;

/**
 * 注册响应
 *
 * @author liuyaowu
 * @date 2022/9/12 21:34
 * @remark
 */
@Data
public class RegisterResponse {

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    /**
     * 注册响应状态:SUCCESS、FAILURE
     */
    private String status;

}
