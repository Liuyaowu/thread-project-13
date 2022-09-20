package com.mobei.register.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 客户端缓存的注册中心的注册表
 *
 * @author liuyaowu
 * @date 2022/9/15 22:08
 * @remark
 */
public class CachedServiceRegistry {

    /**
     * 从注册中心拉取服务注册表的间隔周期
     */
    private static final Long SERVICE_REGISTRY_FETCH_INTERVAL = 30 * 1000L;

    private FetchDeltaRegistryWorker fetchDeltaRegistryWorker;

    /**
     * http通信组件
     */
    private HttpSender httpSender;

    /**
     * 客户端缓存的注册中心的注册表: 服务名称 -> (服务实例id -> 服务实例)
     */
    private Map<String, Map<String, ServiceInstance>> registry = new HashMap<>();

    private RegisterClient registerClient;

    /**
     * 构造函数
     *
     * @param registerClient
     * @param httpSender
     */
    public CachedServiceRegistry(RegisterClient registerClient, HttpSender httpSender) {
        this.fetchDeltaRegistryWorker = new FetchDeltaRegistryWorker();
        this.registerClient = registerClient;
        this.httpSender = httpSender;
    }

    /**
     * 初始化
     */
    public void initialize() {
        // 启动全量拉取注册表的线程
        FetchFullRegistryWorker fetchFullRegistryWorker = new FetchFullRegistryWorker();
        fetchFullRegistryWorker.start();

        // 启动增量拉取注册表的线程
        this.fetchDeltaRegistryWorker.start();
    }

    /**
     * 销毁这个组件
     */
    public void destroy() {
        this.fetchDeltaRegistryWorker.interrupt();
    }

    /**
     * 全量拉取注册表的后台线程
     */
    private class FetchFullRegistryWorker extends Thread {
        @Override
        public void run() {
            // 拉取全量注册表
            registry = httpSender.fetchFullRegistry();
        }
    }

    /**
     * 增量拉取注册表的后台线程
     */
    private class FetchDeltaRegistryWorker extends Thread {

        @Override
        public void run() {
            while (registerClient.isRunning()) {
                try {
                    Thread.sleep(SERVICE_REGISTRY_FETCH_INTERVAL);

                    /**
                     * 拉取回来的是最近3分钟变化的服务实例
                     *
                     * 增量注册表:一类是注册，一类是删除.如果是注册的话，就判断一下这个服务实例是否在这个本地缓存的注册表中
                     * 如果不在的话，就放到本地缓存注册表里去.如果是删除的话，就看一下，如果服务实例存在，就给删除了
                     */
                    DeltaRegistry deltaRegistry = httpSender.fetchDeltaRegistry();

                    // 我们这里其实是要大量的修改本地缓存的注册表，所以此处需要加锁
                    synchronized (registry) {
                        mergeDeltaRegistry(deltaRegistry.getRecentlyChangedQueue());
                    }

                    // 再检查一下，跟服务端的注册表的服务实例的数量相比，是否是一致的
                    // 封装一下增量注册表的对象，也就是拉取增量注册表的时候，一方面是返回那个数据
                    // 另外一方面，是要那个对应的register-server端的服务实例的数量
                    Long serverSideTotalCount = deltaRegistry.getServiceInstanceTotalCount();

                    Long clientSideTotalCount = 0L;
                    for (Map<String, ServiceInstance> serviceInstanceMap : registry.values()) {
                        clientSideTotalCount += serviceInstanceMap.size();
                    }

                    if (serverSideTotalCount.compareTo(clientSideTotalCount) != 0) {
                        // 重新拉取全量注册表进行纠正
                        registry = httpSender.fetchFullRegistry();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 合并增量注册表到本地缓存注册表里去
         *
         * @param deltaRegistry
         */
        private void mergeDeltaRegistry(LinkedList<RecentlyChangedServiceInstance> deltaRegistry) {
            String serviceName, serviceInstanceId;
            for (RecentlyChangedServiceInstance recentlyChangedItem : deltaRegistry) {
                serviceName = recentlyChangedItem.serviceInstance.getServiceName();
                serviceInstanceId = recentlyChangedItem.serviceInstance.getServiceInstanceId();
                // 如果是注册操作的话
                if (ServiceInstanceOperation.REGISTER.equals(recentlyChangedItem.serviceInstanceOperation)) {
                    Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
                    if (serviceInstanceMap == null) {
                        serviceInstanceMap = new HashMap<>();
                        registry.put(serviceName, serviceInstanceMap);
                    }

                    ServiceInstance serviceInstance = serviceInstanceMap.get(serviceInstanceId);
                    if (serviceInstance == null) {
                        serviceInstanceMap.put(serviceInstanceId, recentlyChangedItem.serviceInstance);
                    }
                } else if (ServiceInstanceOperation.REMOVE.equals(recentlyChangedItem.serviceInstanceOperation)) {
                    // 删除操作
                    Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
                    if (serviceInstanceMap != null) {
                        serviceInstanceMap.remove(serviceInstanceId);
                    }
                }
            }
        }

    }

    /**
     * 服务实例操作
     */
    interface ServiceInstanceOperation {
        /**
         * 注册
         */
        String REGISTER = "register";
        /**
         * 删除
         */
        String REMOVE = "REMOVE";
    }

    /**
     * 获取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    /**
     * 最近变更的实例信息
     */
    static class RecentlyChangedServiceInstance {

        /**
         * 服务实例
         */
        ServiceInstance serviceInstance;
        /**
         * 发生变更的时间戳
         */
        Long changedTimestamp;
        /**
         * 变更操作
         */
        String serviceInstanceOperation;

        public RecentlyChangedServiceInstance(ServiceInstance serviceInstance, Long changedTimestamp, String serviceInstanceOperation) {
            this.serviceInstance = serviceInstance;
            this.changedTimestamp = changedTimestamp;
            this.serviceInstanceOperation = serviceInstanceOperation;
        }

        @Override
        public String toString() {
            return "RecentlyChangedServiceInstance [serviceInstance=" + serviceInstance + ", changedTimestamp="
                    + changedTimestamp + ", serviceInstanceOperation=" + serviceInstanceOperation + "]";
        }
    }

}