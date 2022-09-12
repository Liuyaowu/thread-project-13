package com.mobei.register.client;

import lombok.Data;

/**
 * 心跳请求
 *
 * @author liuyaowu
 * @date 2022/9/12 21:58
 * @remark
 */
@Data
public class HeartbeatRequest {

    /**
     * 服务实例id
     */
    private String serviceInstanceId;

}
