package com.mobei.register.server.core;

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
     * 判断服务实例死亡的周期
     */
    public static final Long NOT_ALIVE_PERIOD = 90 * 1000L;

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

    /**
     * 契约信息
     */
    private Lease lease;

    public ServiceInstance() {
        this.lease = new Lease();
    }

    public void renew() {
        this.lease.renew();
    }

    /**
     * 判断服务实例契约是否存活
     *
     * @return
     */
    public Boolean isAlive() {
        return this.lease.isAlive();
    }

    /**
     * 契约
     * <p>
     * 维护了一个服务实例跟当前注册中心之间的联系,包括心跳时间、创建时间等等
     */
    @Data
    private class Lease {

        /**
         * 最近一次心跳的时间
         */
        private volatile Long latestHeartbeatTime = System.currentTimeMillis();

        /**
         * 续约
         */
        public void renew() {
            this.latestHeartbeatTime = System.currentTimeMillis();
            System.out.println("服务实例[ " + serviceInstanceId + " ]续约成功,当前时间: " + latestHeartbeatTime);
        }

        /**
         * 判断服务实例契约是否存活
         *
         * @return
         */
        public Boolean isAlive() {
            boolean isAlive = System.currentTimeMillis() - latestHeartbeatTime < NOT_ALIVE_PERIOD;
            System.out.println(isAlive ? "服务实例存活" : "服务实例下线");
            return isAlive;
        }

    }

}
