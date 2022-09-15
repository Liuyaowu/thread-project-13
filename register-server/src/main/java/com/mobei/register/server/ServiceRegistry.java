package com.mobei.register.server;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务注册表
 *
 * @author liuyaowu
 * @date 2022/9/13 7:00
 * @remark
 */
@Slf4j
public class ServiceRegistry {

    private static ServiceRegistry instance = new ServiceRegistry();

    /**
     * 注册表: 服务名称 -> (服务实例id -> 服务实例)
     */
    private Map<String, Map<String, ServiceInstance>> registry = new HashMap<>();

    private ServiceRegistry() {

    }

    public static ServiceRegistry getInstance() {
        return instance;
    }

    /**
     * 获取服务实例
     *
     * @param serviceName
     * @param serviceInstanceId
     * @return
     */
    public synchronized ServiceInstance getServiceInstance(String serviceName, String serviceInstanceId) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
        return serviceInstanceMap.get(serviceInstanceId);
    }

    /**
     * 获取注册表
     *
     * @return
     */
    public synchronized Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    /**
     * 从注册表删除一个服务实例
     *
     * @param serviceName
     * @param serviceInstanceId
     */
    public synchronized void remove(String serviceName, String serviceInstanceId) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
        serviceInstanceMap.remove(serviceInstanceId);
        System.out.println("服务实例[" + serviceInstanceId + "]从注册表中被摘除");
    }

    /**
     * 注册服务实例
     *
     * @param serviceInstance
     */
    public synchronized void register(ServiceInstance serviceInstance) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceInstance.getServiceName());
        if (serviceInstanceMap == null) {
            serviceInstanceMap = new HashMap<>();
            registry.put(serviceInstance.getServiceName(), serviceInstanceMap);
        }

        serviceInstanceMap.put(serviceInstance.getServiceInstanceId(), serviceInstance);

        System.out.println("服务实例[ " + serviceInstance + " ]完成注册");
        System.out.println("服务注册完成后的注册表信息:[ " + registry + " ]");
    }

}
