package com.mobei.register.server;

import java.util.Map;

/**
 * 负责接受client发送过来的请求,spring cloud eureka中用的组件是jersey
 *
 * @author liuyaowu
 * @date 2022/9/13 6:57
 * @remark
 */
public class RegisterServerController {

    private ServiceRegistry registry = ServiceRegistry.getInstance();

    /**
     * 服务注册
     *
     * @param registerRequest 注册请求
     * @return 注册响应
     */
    public RegisterResponse register(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();

        try {
            // 在注册表中加入这个服务实例
            ServiceInstance serviceInstance = new ServiceInstance();
            serviceInstance.setHostname(registerRequest.getHostname());
            serviceInstance.setIp(registerRequest.getIp());
            serviceInstance.setPort(registerRequest.getPort());
            serviceInstance.setServiceInstanceId(registerRequest.getServiceInstanceId());
            serviceInstance.setServiceName(registerRequest.getServiceName());

            registry.register(serviceInstance);

            // 更新自我保护机制的阈值
            synchronized (SelfProtectionPolicy.class) {
                SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();

                /**
                 * 期待每分钟心跳次数:30秒一次心跳,那么每注册一个服务实例的话,服务注册中心每分钟接收到的心跳次数应该加2
                 *
                 * todo 这里硬编码了,正常来说心跳次数是可以调整的,那么每分钟的心跳次数应该是动态计算的
                 */
                long expectedHeartbeatRate = selfProtectionPolicy.getExpectedHeartbeatRate();
                selfProtectionPolicy.setExpectedHeartbeatRate(expectedHeartbeatRate + 2);
                selfProtectionPolicy.setExpectedHeartbeatThreshold((long) (expectedHeartbeatRate * 0.85));
            }

            registerResponse.setStatus(RegisterResponse.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            registerResponse.setStatus(RegisterResponse.FAILURE);
        }

        return registerResponse;
    }

    /**
     * 发送心跳
     *
     * @param heartbeatRequest 心跳请求
     * @return 心跳响应
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest heartbeatRequest) {
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();

        try {
            // 对服务实例进行续约
            ServiceInstance serviceInstance = registry.getServiceInstance(
                    heartbeatRequest.getServiceName(), heartbeatRequest.getServiceInstanceId());
            serviceInstance.renew();

            // 记录一下每分钟的心跳的次数
            HeartbeatMeasureRate heartbeatMeasureRate = HeartbeatMeasureRate.getInstance();
            heartbeatMeasureRate.increment();

            heartbeatResponse.setStatus(HeartbeatResponse.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            heartbeatResponse.setStatus(HeartbeatResponse.FAILURE);
        }

        return heartbeatResponse;
    }

    /**
     * 拉取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> fetchServiceRegistry() {
        return registry.getRegistry();
    }

    /**
     * 服务下线
     */
    public void cancel(String serviceName, String serviceInstanceId) {
        // 从服务注册中摘除实例
        registry.remove(serviceName, serviceInstanceId);

        // 更新自我保护机制的阈值
        synchronized (SelfProtectionPolicy.class) {
            SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();
            long expectedHeartbeatRate = selfProtectionPolicy.getExpectedHeartbeatRate();
            selfProtectionPolicy.setExpectedHeartbeatRate(expectedHeartbeatRate - 2);
            selfProtectionPolicy.setExpectedHeartbeatThreshold((long) (expectedHeartbeatRate * 0.85));
        }
    }

}
