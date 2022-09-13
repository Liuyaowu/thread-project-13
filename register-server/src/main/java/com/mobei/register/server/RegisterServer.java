package com.mobei.register.server;

import java.util.UUID;

/**
 * 代表了服务注册中心
 *
 * @author liuyaowu
 * @date 2022/9/13 7:27
 * @remark
 */
public class RegisterServer {

    public static void main(String[] args) {
        String serviceInstanceId = UUID.randomUUID().toString().replace("-", "");

        // 模拟发起服务注册请求
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setHostname("inventory-service-01");
        registerRequest.setIp("192.168.31.208");
        registerRequest.setPort(9000);
        registerRequest.setServiceName("inventory-service");
        registerRequest.setServiceInstanceId(serviceInstanceId);

        RegisterServerController registerServerController = new RegisterServerController();
        registerServerController.register(registerRequest);

        // 模拟心跳,完成续约
        HeartbeatRequest heartbeatRequest;

        while (true) {
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
