package com.mobei.register.server;

import lombok.Data;

/**
 * 注册请求
 *
 * @author liuyaowu
 * @date 2022/9/12 21:32
 * @remark
 */
@Data
public class RegisterRequest {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务所在机器的IP地址
     */
    private String ip;

    /**
     * 服务所在机器的主机名
     */
    private String hostname;

    /**
     * 服务监听的端口号
     */
    private int port;

    /**
     * 服务实例id
     */
    private String serviceInstanceId;

}
