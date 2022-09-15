package com.mobei.register.client;

import java.util.HashMap;
import java.util.Map;

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
        System.out.println("服务实例: " + request + " 发送心跳请求...");

        // 收到register-server的响应之后,封装一个response对象返回
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();
        heartbeatResponse.setStatus(HeartbeatResponse.SUCCESS);

        return heartbeatResponse;
    }

    /**
     * 从注册中心拉取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> fetchServiceRegistry() {
        Map<String, Map<String, ServiceInstance>> registry = new HashMap<>();

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setHostname("finance-service-01");
        serviceInstance.setIp("192.168.31.1207");
        serviceInstance.setPort(9000);
        serviceInstance.setServiceInstanceId("FINANCE-SERVICE-192.168.31.207:9000");
        serviceInstance.setServiceName("FINANCE-SERVICE");

        Map<String, ServiceInstance> serviceInstances = new HashMap<>();
        serviceInstances.put("FINANCE-SERVICE-192.168.31.207:9000", serviceInstance);

        registry.put("FINANCE-SERVICE", serviceInstances);

        System.out.println("拉取注册表：" + registry);

        return registry;
    }

    /**
     * 服务下线
     */
    public void cancel(String serviceName, String serviceInstanceId) {
        System.out.println("服务下线,服务名称[" + serviceName + "],服务实例id[" + serviceInstanceId + "]");
    }

}
