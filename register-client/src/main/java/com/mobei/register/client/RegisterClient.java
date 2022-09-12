package com.mobei.register.client;

import java.util.UUID;

/**
 * @author liuyaowu
 * @date 2022/9/12 21:26
 * @remark
 */
public class RegisterClient {

    /**
     * 服务实例id
     */
    String serviceInstanceId;

    public RegisterClient() {
        serviceInstanceId = UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 1.开启一个线程向register-server发送请求,注册该服务
     * 2.注册成功之后,开启另外一个线程去发送心跳
     */
    public void start() {
        new RegisterClientWorker(serviceInstanceId).start();
    }

}
