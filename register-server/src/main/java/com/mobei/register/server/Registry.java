package com.mobei.register.server;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册表
 *
 * @author liuyaowu
 * @date 2022/9/13 7:00
 * @remark
 */
@Slf4j
public class Registry {

    private static Registry instance = new Registry();

    /**
     * 注册表: 服务名称 -> (服务实例id -> 服务实例)
     */
    private Map<String, Map<String, ServiceInstance>> registry = new HashMap<>();

    private Registry() {

    }

    public static Registry getInstance() {
        return instance;
    }

    /**
     * 获取服务实例
     *
     * @param serviceName
     * @param serviceInstanceId
     * @return
     */
    public ServiceInstance getServiceInstance(String serviceName, String serviceInstanceId) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
        return serviceInstanceMap.get(serviceInstanceId);
    }

    /**
     * 注册服务实例
     *
     * @param serviceInstance
     */
    public void register(ServiceInstance serviceInstance) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceInstance.getServiceName());
        if (serviceInstanceMap == null) {
            serviceInstanceMap = new HashMap<>();
            registry.put(serviceInstance.getServiceInstanceId(), serviceInstanceMap);
        }

        serviceInstanceMap.put(serviceInstance.getServiceName(), serviceInstance);

        System.out.println("服务实例[ " + serviceInstance + " ]完成注册");
        System.out.println("服务注册完成后的注册表信息:[ " + registry + " ]");
    }

}
