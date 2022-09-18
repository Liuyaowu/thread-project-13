package com.mobei.register.server;

import java.util.Map;

/**
 * 微服务存活状态监控组件
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

    private Daemon daemon;

    public ServiceAliveMonitor() {
        ThreadGroup daemonThreadGroup = new ThreadGroup("daemon");

        this.daemon = new Daemon(daemonThreadGroup, "ServiceAliveMonitor");

        /**
         * 设置为守护线程(后台线程)
         *
         * 守护线程:不会阻止JVM进程退出,会跟着JVM一起结束.不设置的话默认为工作线程,只要线程没结束JVM就不会退出
         */
        daemon.setDaemon(true);
        /**
         * 设置线程名称
         */
//        daemon.setName("ServiceAliveMonitor");

        /**
         * 设置优先级,并没什么用
         */
        daemon.setPriority(10);
    }

    public void start() {
        daemon.start();
    }

    /**
     * 负责监控微服务存活状态的后台线程
     */
    private class Daemon extends Thread {

        private ServiceRegistry registry = ServiceRegistry.getInstance();

        public Daemon(ThreadGroup daemonThreadGroup, String threadName) {
            super(daemonThreadGroup, threadName);
        }

        @Override
        public void run() {
            Map<String, Map<String, ServiceInstance>> registryMap = null;

            while (true) {
                try {
                    // 可以判断一下是否要开启自我保护机制
                    SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();
                    if (selfProtectionPolicy.isEnable()) {
                        Thread.sleep(CHECK_ALIVE_INTERVAL);
                        continue;
                    }

                    registryMap = registry.getRegistry();

                    for (String serviceName : registryMap.keySet()) {
                        Map<String, ServiceInstance> serviceInstanceMap = registryMap.get(serviceName);

                        for (ServiceInstance serviceInstance : serviceInstanceMap.values()) {
                            /**
                             * 说明服务实例距离上一次发送心跳已经超过90秒了,认为这个服务就死了,从注册表中摘除这个服务实例
                             */
                            if (!serviceInstance.isAlive()) {
                                registry.remove(serviceName, serviceInstance.getServiceInstanceId());
                                long expectedHeartbeatRate = selfProtectionPolicy.getExpectedHeartbeatRate();
                                // 更新自我保护机制的阈值
                                synchronized (SelfProtectionPolicy.class) {
                                    selfProtectionPolicy.setExpectedHeartbeatRate(expectedHeartbeatRate - 2);
                                    selfProtectionPolicy.setExpectedHeartbeatThreshold((long) (expectedHeartbeatRate * 0.85));
                                }
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
