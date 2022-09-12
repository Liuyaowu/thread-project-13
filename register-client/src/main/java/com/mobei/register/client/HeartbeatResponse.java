package com.mobei.register.client;

import lombok.Data;

/**
 * 心跳响应结果
 *
 * @author liuyaowu
 * @date 2022/9/12 22:01
 * @remark
 */
@Data
public class HeartbeatResponse {

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    /**
     * 心跳响应状态:SUCCESS、FAILURE
     */
    private String status;

}
