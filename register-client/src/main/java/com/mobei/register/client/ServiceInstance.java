package com.mobei.register.client;

import lombok.Data;

/**
 * 代表一个服务实例,包含一个服务实例的所有信息,比如服务名称、IP地址、hostname、端口号、服务实例id,
 * 服务契约信息(lease)
 *
 * @author liuyaowu
 * @date 2022/9/13 7:02
 * @remark
 */
@Data
public class ServiceInstance {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 服务实例id
     */
    private String serviceInstanceId;

}
