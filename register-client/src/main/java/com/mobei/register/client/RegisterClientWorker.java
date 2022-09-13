package com.mobei.register.client;

/**
 * @author liuyaowu
 * @date 2022/9/12 21:54
 * @remark
 */
public class RegisterClientWorker extends Thread {

    public static final String SERVICE_NAME = "inventory-service";
    public static final String IP = "192.168.31.207";
    public static final String HOSTNAME = "inventory01";
    public static final Integer PORT = 9000;

    /**
     * http通信组件
     */
    private HttpSender httpSender;
    /**
     * 是否完成服务注册
     */
    private Boolean finishedRegister;

    private String serviceInstanceId;

    public RegisterClientWorker(String serviceInstanceId) {
        httpSender = new HttpSender();
        this.finishedRegister = false;
        this.serviceInstanceId = serviceInstanceId;
    }

    /**
     * 获取当前机器的信息,包括IP地址、hostname、以及配置的这个服务监听的端口号,
     * 从配置文件里可以拿到
     */
    @Override
    public void run() {

        if (!finishedRegister) {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setServiceName(SERVICE_NAME);
            registerRequest.setIp(IP);
            registerRequest.setHostname(HOSTNAME);
            registerRequest.setPort(PORT);
            registerRequest.setServiceInstanceId(serviceInstanceId);

            RegisterResponse registerResponse = httpSender.register(registerRequest);
            System.out.println("服务注册响应结果: " + registerResponse);

            /**
             * 注册成功
             */
            if (RegisterResponse.SUCCESS.equals(registerResponse.getStatus())) {
                this.finishedRegister = true;
            } else {
                return;
            }
        }

        // 如果注册成功了,进入while true
        if (finishedRegister) {
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            heartbeatRequest.setServiceName(SERVICE_NAME);
            heartbeatRequest.setServiceInstanceId(serviceInstanceId);
            HeartbeatResponse heartbeatResponse;

            while (true) {
                try {
                    // 发送心跳
                    heartbeatResponse = httpSender.heartbeat(heartbeatRequest);
                    System.out.println("心跳结果: " + heartbeatResponse);
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
