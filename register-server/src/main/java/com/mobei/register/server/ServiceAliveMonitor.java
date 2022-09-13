package com.mobei.register.server;

import java.util.Map;

/**
 * 微服务存货状态监控组件
 *
 * @author liuyaowu
 * @date 2022/9/13 20:53
 * @remark
 */
public class ServiceAliveMonitor {

    /**
     * 检查服务实例是否存活的周期
     */
    public static final Long CHECK_ALIVE_INTERVAL = 60 * 1000L;

    private Daemon daemon = new Daemon();

    public void start() {
        daemon.start();
    }

    /**
     * 负责监控微服务存活状态的后台线程
     */
    private class Daemon extends Thread {

       private Registry registry = Registry.getInstance();

        @Override
        public void run() {
            Map<String, Map<String, ServiceInstance>> registry;
            while (true) {
                try {
                    registry = this.registry.getRegistry();
                    for (Map.Entry<String, Map<String, ServiceInstance>> entry : registry.entrySet()) {
                        Map<String, ServiceInstance> serviceInstanceMap = entry.getValue();
                        for (Map.Entry<String, ServiceInstance> serviceInstanceEntry : serviceInstanceMap.entrySet()) {
                            ServiceInstance serviceInstance = serviceInstanceEntry.getValue();

                            /**
                             * 服务实例距离上次心跳时间超过了默认的存活周期时长,摘除服务
                             */
                            if (!serviceInstance.isAlive()) {
                                this.registry.remove(serviceInstance.getServiceName(), serviceInstance.getServiceInstanceId());
                            }
                        }
                    }
                    Thread.sleep(CHECK_ALIVE_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
