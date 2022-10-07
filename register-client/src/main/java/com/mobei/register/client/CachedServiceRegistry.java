package com.mobei.register.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 客户端缓存的注册中心的注册表
 *
 * @author liuyaowu
 * @date 2022/9/15 22:08
 * @remark
 */
public class CachedServiceRegistry {
    /**
     * 服务注册表拉取间隔时间
     */
    private static final Long SERVICE_REGISTRY_FETCH_INTERVAL = 30 * 1000L;

    /**
     * 客户端缓存的所有的服务实例的信息
     */
    private AtomicStampedReference<Applications> asrApplications;
    /**
     * 负责定时拉取注册表到客户端进行缓存的后台线程
     */
    private FetchDeltaRegistryWorker fetchDeltaRegistryWorker;
    /**
     * RegisterClient
     */
    private RegisterClient registerClient;
    /**
     * http通信组件
     */
    private HttpSender httpSender;
    /**
     * 代表了当前的本地缓存的服务注册表的一个版本号
     */
    private AtomicLong applicationsVersion = new AtomicLong(0L);
    /**
     * 本地缓存注册表读写锁
     */
    private ReentrantReadWriteLock applicationsLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.WriteLock writeLock = applicationsLock.writeLock();
    private ReentrantReadWriteLock.ReadLock readLock = applicationsLock.readLock();

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
        this.asrApplications = new AtomicStampedReference<>(new Applications(), 0);
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
            // 这个操作要走网络，但是不知道为什么抽风了，此时就是一直卡住，数据没有返回回来
            // 卡在这儿了，卡了几分钟
            // 此时的这个数据是一个旧的版本，里面仅仅包含了30个服务实例
            // 全量拉注册表的线程突然苏醒过来了，此时将30个服务实例的旧版本的数据赋值给了本地缓存注册表
            // 将校对正确的数据更新成了错误的旧数据

            // 一定要在发起网络请求之前，先拿到一个当时的版本号
            // 接着在这里发起网络请求，此时可能会有别的线程来修改这个注册表，更新版本，在这个期间
            // 必须是发起网络请求之后，这个注册表的版本没有被人修改过，此时他才能去修改
            // 如果在这个期间，有人修改过注册表，版本不一样了，此时就直接if不成立，不要把你拉取到的
            // 旧版本的注册表给设置进去
            fetchFullRegistry();
        }
    }

    /**
     * 拉取全量注册表到本地
     */
    private void fetchFullRegistry() {
        // version = 0
        Long expectedVersion = applicationsVersion.get();
        Applications fetchedApplications = httpSender.fetchFullRegistry();

        // version = 1
        if (applicationsVersion.compareAndSet(expectedVersion, expectedVersion + 1)) {
            // 防止网络卡顿造成的旧数据去更新新的数据
            while (true) {
                Applications expectedApplications = asrApplications.getReference();
                int expectedStamp = asrApplications.getStamp();
                if (asrApplications.compareAndSet(expectedApplications, fetchedApplications,
                        expectedStamp, expectedStamp + 1)) {
                    break;
                }
            }
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

                    // 拉取回来的是最近3分钟变化的服务实例
                    // 先拉了一个增量注册表，发现跟本地合并之后，条数不对
                    Long expectedVersion = applicationsVersion.get();
                    DeltaRegistry deltaRegistry = httpSender.fetchDeltaRegistry();

                    if (applicationsVersion.compareAndSet(expectedVersion, expectedVersion + 1)) {
                        // 一类是注册，一类是删除
                        // 如果是注册的话，就判断一下这个服务实例是否在这个本地缓存的注册表中
                        // 如果不在的话，就放到本地缓存注册表里去
                        // 如果是删除的话，就看一下，如果服务实例存在，就给删除了

                        // 我们这里其实是要大量的修改本地缓存的注册表，所以此处需要加锁
                        mergeDeltaRegistry(deltaRegistry);

                        // 再检查一下，跟服务端的注册表的服务实例的数量相比，是否是一致的
                        // 封装一下增量注册表的对象，也就是拉取增量注册表的时候，一方面是返回那个数据
                        // 另外一方面，是要那个对应的register-server端的服务实例的数量
                        reconcileRegistry(deltaRegistry);
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
        private void mergeDeltaRegistry(DeltaRegistry deltaRegistry) {
            try {
                writeLock.lock();
                Map<String, Map<String, ServiceInstance>> registry = asrApplications.getReference().getRegistry();
                LinkedList<RecentlyChangedServiceInstance> recentlyChangedServiceInstances = deltaRegistry.getRecentlyChangedQueue();
                String serviceName, serviceInstanceId;
                for (RecentlyChangedServiceInstance recentlyChangedItem : recentlyChangedServiceInstances) {
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
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * 校对调整注册表
         *
         * @param deltaRegistry
         */
        private void reconcileRegistry(DeltaRegistry deltaRegistry) {
            // 再检查一下，跟服务端的注册表的服务实例的数量相比，是否是一致的
            // 封装一下增量注册表的对象，也就是拉取增量注册表的时候，一方面是返回那个数据
            // 另外一方面，是要那个对应的register-server端的服务实例的数量
            Long serverSideTotalCount = deltaRegistry.getServiceInstanceTotalCount();
            Map<String, Map<String, ServiceInstance>> registry = asrApplications.getReference().getRegistry();
            int expectedStamp = asrApplications.getStamp();

            Long clientSideTotalCount = 0L;
            for (Map<String, ServiceInstance> serviceInstanceMap : registry.values()) {
                clientSideTotalCount += serviceInstanceMap.size();
            }

            if (serverSideTotalCount.compareTo(clientSideTotalCount) != 0) {
                // 重新拉取全量注册表进行纠正
                Applications fetchedApplications = httpSender.fetchFullRegistry();

                while (true) {
                    // 客户端缓存的实例
                    Applications expectedApplications = asrApplications.getReference();

                    /**
                     * 用本地的AtomicReference和它里面取出来的值做CAS比较,如果引用对象是同一个,说明没有其它线程更改过,
                     * 那么就把引用对象更新为刚刚拉取到的实例-fetchedApplications,退出循环
                     */
                    if (asrApplications.compareAndSet(expectedApplications,
                            fetchedApplications,
                            expectedStamp,
                            expectedStamp++)) {
                        break;
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
        try {
            readLock.lock();
            return asrApplications.getReference().getRegistry();
        } finally {
            readLock.unlock();
        }
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
            return "RecentlyChangedServiceInstance [serviceInstance=" + serviceInstance +
                    ", changedTimestamp=" + changedTimestamp +
                    ", serviceInstanceOperation=" + serviceInstanceOperation + "]";
        }
    }

}
