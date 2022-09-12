package com.mobei.register.client;

/**
 * 负责发送各种http请求的组件
 *
 * @author liuyaowu
 * @date 2022/9/12 21:29
 * @remark
 */
public class HttpSender {

    /**
     * 发送注册请求
     *
     * @param request
     * @return
     */
    public RegisterResponse register(RegisterRequest request) {
        /**
         * TODO
         * 基于类似HttpClient开源网络包,去构造一个请求,放入服务实例的信息,
         * 比如服务名称、IP地址、端口号,通过请求发送过去
         */
        System.out.println("发送请求进行注册...");

        // 收到register-server的响应之后,封装一个response对象返回
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setStatus(RegisterResponse.SUCCESS);

        return registerResponse;
    }

    /**
     * 发送心跳请求
     *
     * @param request
     * @return
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest request) {
        System.out.println("发送心跳请求...");

        // 收到register-server的响应之后,封装一个response对象返回
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();
        heartbeatResponse.setStatus(HeartbeatResponse.SUCCESS);

        return heartbeatResponse;
    }

}
