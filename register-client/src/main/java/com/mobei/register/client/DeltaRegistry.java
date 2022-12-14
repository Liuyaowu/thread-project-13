package com.mobei.register.client;

import com.mobei.register.client.CachedServiceRegistry.RecentlyChangedServiceInstance;

import java.util.Queue;

/**
 * @author liuyaowu
 * @date 2022/9/20 7:56
 * @remark
 */
public class DeltaRegistry {

    /**
     * 增量变更的服务实例
     */
    private Queue<RecentlyChangedServiceInstance> recentlyChangedQueue;
    /**
     * 服务实例总数
     */
    private Long serviceInstanceTotalCount;

    public DeltaRegistry(Queue<RecentlyChangedServiceInstance> recentlyChangedQueue,
                         Long serviceInstanceTotalCount) {
        this.recentlyChangedQueue = recentlyChangedQueue;
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }

    public Queue<RecentlyChangedServiceInstance> getRecentlyChangedQueue() {
        return recentlyChangedQueue;
    }

    public void setRecentlyChangedQueue(Queue<RecentlyChangedServiceInstance> recentlyChangedQueue) {
        this.recentlyChangedQueue = recentlyChangedQueue;
    }

    public Long getServiceInstanceTotalCount() {
        return serviceInstanceTotalCount;
    }

    public void setServiceInstanceTotalCount(Long serviceInstanceTotalCount) {
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }

}
