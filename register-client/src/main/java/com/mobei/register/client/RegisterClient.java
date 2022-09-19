package com.mobei.register.client;

import java.util.UUID;

/**
 * @author liuyaowu
 * @date 2022/9/12 21:26
 * @remark
 */
public class RegisterClient {

    public static final String SERVICE_NAME = "inventory-service";
    public static final String IP = "192.168.31.207";
    public static final String HOSTNAME = "inventory01";
    public static final int PORT = 9000;
    private static final Long HEARTBEAT_INTERVAL = 30 * 1000L;

    /**
     * 服务实例id
     */
    private String serviceInstanceId;
    /**
     * http通信组件
     */
    private HttpSender httpSender;
    /**
     * 心跳线程
     */
    private HeartbeatWorker heartbeatWorker;
    /**
     * 服务实例是否在运行
     */
    private volatile Boolean isRunning;
    /**
     * 客户端缓存的注册表
     */
    private CachedServiceRegistry registry;

    public RegisterClient() {
        this.serviceInstanceId = UUID.randomUUID().toString().replace("-", "");
        this.httpSender = new HttpSender();
        this.heartbeatWorker = new HeartbeatWorker();
        this.isRunning = true;
        this.registry = new CachedServiceRegistry(this, httpSender);
    }

    /**
     * 1.开启一个线程向register-server发送请求,注册该服务
     * 2.注册成功之后,开启另外一个线程去发送心跳
     */
    public void start() {
        try {
            RegisterWorker registerWorker = new RegisterWorker();
            registerWorker.start();
            /**
             * 等待注册完成
             */
            registerWorker.join();

            /**
             * 注册完成开启心跳
             */
            heartbeatWorker.start();

            /**
             * 初始化本地缓存注册服务
             */
            this.registry.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止RegisterClient组件
     */
    public void shutdown() {
        this.isRunning = false;
        this.heartbeatWorker.interrupt();
        this.registry.destroy();
        this.httpSender.cancel(SERVICE_NAME, serviceInstanceId);
        System.out.println("停止client");
    }

    private class RegisterWorker extends Thread {
        /**
         * 获取当前机器的信息,包括IP地址、hostname、以及配置的这个服务监听的端口号,
         * 从配置文件里可以拿到
         */
        @Override
        public void run() {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setServiceName(SERVICE_NAME);
            registerRequest.setIp(IP);
            registerRequest.setHostname(HOSTNAME);
            registerRequest.setPort(PORT);
            registerRequest.setServiceInstanceId(serviceInstanceId);

            RegisterResponse registerResponse = httpSender.register(registerRequest);
            System.out.println("服务注册响应结果: " + registerResponse);
        }
    }

    /**
     * 心跳线程
     */
    private class HeartbeatWorker extends Thread {
        @Override
        public void run() {
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            heartbeatRequest.setServiceName(SERVICE_NAME);
            heartbeatRequest.setServiceInstanceId(serviceInstanceId);
            HeartbeatResponse heartbeatResponse;

            while (isRunning) {
                try {
                    // 发送心跳
                    heartbeatResponse = httpSender.heartbeat(heartbeatRequest);
                    System.out.println("心跳结果: " + heartbeatResponse);
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Boolean getRunning() {
        return isRunning;
    }

}
