package com.mobei.register.client;

import java.util.UUID;

/**
 * 负责向register-server发起注册申请的线程
 *
 * @author liuyaowu
 * @date 2022/9/12 21:28
 * @remark
 */
public class RegisterWorker  extends Thread {

    public static final String SERVICE_NAME = "inventory-service";
    public static final String IP = "192.168.31.207";
    public static final String HOSTNAME = "inventory01";
    public static final Integer PORT = 9000;

    private HttpSender httpSender;

    public RegisterWorker() {
        httpSender = new HttpSender();
    }

    /**
     * 获取当前机器的信息,包括IP地址、hostname、以及配置的这个服务监听的端口号,
     * 从配置文件里可以拿到
     */
    @Override
    public void run() {
        RegisterRequest request = new RegisterRequest();
        request.setServiceName(SERVICE_NAME);
        request.setIp(IP);
        request.setHostname(HOSTNAME);
        request.setPort(PORT);
        request.setServiceInstanceId(UUID.randomUUID().toString().replace("-", ""));

        RegisterResponse response = httpSender.register(request);
        System.out.println("服务注册响应结果: " + response);
    }

}
