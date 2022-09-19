package com.mobei.register.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端缓存的注册中心的注册表
 *
 * @author liuyaowu
 * @date 2022/9/15 22:08
 * @remark
 */
public class CachedServiceRegistry {

    /**
     * 从注册中心拉取服务注册表的间隔周期
     */
    private static final Long SERVICE_REGISTRY_FETCH_INTERVAL = 30 * 1000L;

    private Daemon daemon;

    /**
     * http通信组件
     */
    private HttpSender httpSender;

    /**
     * 客户端缓存的注册中心的注册表: 服务名称 -> (服务实例id -> 服务实例)
     */
    private Map<String, Map<String, ServiceInstance>> registry = new HashMap<>();

    private RegisterClient registerClient;

    public CachedServiceRegistry(RegisterClient registerClient, HttpSender httpSender) {
        this.daemon = new Daemon();
        this.registerClient = registerClient;
        this.httpSender = httpSender;
    }

    public void initialize() {
        this.daemon.start();
    }

    public void destroy() {
        this.daemon.interrupt();
    }

    /**
     * 负责定时拉取注册表到本地进行缓存
     */
    private class Daemon extends Thread {
        @Override
        public void run() {
            while (registerClient.getRunning()) {
                try {
                    System.out.println("从注册中心拉取注册表");
                    httpSender.fetchServiceRegistry();

                    Thread.sleep(SERVICE_REGISTRY_FETCH_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
