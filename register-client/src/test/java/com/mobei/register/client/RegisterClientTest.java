package com.mobei.register.client;

/**
 * @author liuyaowu
 * @date 2022/9/12 22:16
 * @remark
 */
public class RegisterClientTest {

    public static void main(String[] args) throws InterruptedException {
        RegisterClient registerClient = new RegisterClient();
        registerClient.start();

        Thread.sleep(3000);

        registerClient.shutdown();
    }


}
