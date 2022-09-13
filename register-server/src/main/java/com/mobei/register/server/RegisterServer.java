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
        String serviceName = "inventory-service";

        // 模拟发起服务注册请求
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setHostname("inventory-service-01");
        registerRequest.setIp("192.168.31.208");
        registerRequest.setPort(9000);
        registerRequest.setServiceName(serviceName);
        registerRequest.setServiceInstanceId(serviceInstanceId);

        RegisterServerController registerServerController = new RegisterServerController();
        registerServerController.register(registerRequest);

        // 模拟心跳,完成续约
        HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
        heartbeatRequest.setServiceName(serviceName);
        heartbeatRequest.setServiceInstanceId(serviceInstanceId);

        registerServerController.heartbeat(heartbeatRequest);

        // 开启一个后台线程检测服务存活状态
        ServiceAliveMonitor serviceAliveMonitor = new ServiceAliveMonitor();
        serviceAliveMonitor.start();

        /**
         * 一般来说,register-server这种东西不会只有一个main线程作为工作线程,
         * 一般来说是一个web工程,部署在一个web服务器中m最核心的工作线程就是
         * 专门用于接收和处理client发送过来的请求的那些工作线程,正常来说,
         * 只要有工作线程,是不会随便退出的,如果说工作线程都停止了,
         * 那么daemon线程会跟着JVM进程一块退出
         *
         * 这里用while true模拟main线程中还有工作线程,不要退出,保证服务一直运行
         */

        while (true) {
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
