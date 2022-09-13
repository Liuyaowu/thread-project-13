package com.mobei.register.server;

/**
 * 负责接受client发送过来的请求,spring cloud eureka中用的组件是jersey
 *
 * @author liuyaowu
 * @date 2022/9/13 6:57
 * @remark
 */
public class RegisterServerController {

    private Registry registry = Registry.getInstance();

    /**
     * 注册服务
     *
     * @param registerRequest
     * @return
     */
    public RegisterResponse register(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();

        try {
            ServiceInstance serviceInstance = new ServiceInstance();
            serviceInstance.setHostname(registerRequest.getHostname());
            serviceInstance.setIp(registerRequest.getIp());
            serviceInstance.setPort(registerRequest.getPort());
            serviceInstance.setServiceName(registerRequest.getServiceName());
            serviceInstance.setServiceInstanceId(registerRequest.getServiceInstanceId());

            registry.register(serviceInstance);

            registerResponse.setStatus(RegisterResponse.SUCCESS);
        } catch (Exception e) {
            registerResponse.setStatus(RegisterResponse.FAILURE);
        }

        return registerResponse;
    }

    /**
     * 注册服务
     *
     * @param heartbeatRequest
     * @return
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest heartbeatRequest) {
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();

        try {
            ServiceInstance serviceInstance = registry.getServiceInstance(
                    heartbeatRequest.getServiceName(),
                    heartbeatRequest.getServiceInstanceId());
            serviceInstance.renew();

            heartbeatResponse.setStatus(RegisterResponse.SUCCESS);
        } catch (Exception e) {
            heartbeatResponse.setStatus(RegisterResponse.FAILURE);
        }

        return heartbeatResponse;
    }

}
