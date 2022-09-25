package com.mobei.register.server;

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
                long expectedHeartbeatRate = selfProtectionPolicy.getExpectedHeartbeatRate();
                selfProtectionPolicy.setExpectedHeartbeatRate(expectedHeartbeatRate - 2);
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

    /**
     * 发送心跳
     *
     * @param heartbeatRequest 心跳请求
     * @return 心跳响应
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest heartbeatRequest) {
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();

        try {
            ServiceInstance serviceInstance = registry.getServiceInstance(
                    heartbeatRequest.getServiceName(), heartbeatRequest.getServiceInstanceId());
            if (serviceInstance != null) {
                serviceInstance.renew();
            }

            // 记录一下每分钟的心跳的次数
            HeartbeatCounter heartbeatMessuredRate = HeartbeatCounter.getInstance();
            heartbeatMessuredRate.increment();

            heartbeatResponse.setStatus(HeartbeatResponse.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            heartbeatResponse.setStatus(HeartbeatResponse.FAILURE);
        }

        return heartbeatResponse;
    }

    /**
     * 拉取全量注册表
     *
     * @return
     */
    public Applications fetchFullRegistry() {
        try {
            registry.readLock();
            return new Applications(registry.getRegistry());
        } finally {
            registry.readUnlock();
        }
    }

    /**
     * 拉取增量注册表
     *
     * @return
     */
    public DeltaRegistry fetchDeltaRegistry() {
        try {
            registry.readLock();
            return registry.getDeltaRegistry();
        } finally {
            registry.readUnlock();
        }
    }

}
