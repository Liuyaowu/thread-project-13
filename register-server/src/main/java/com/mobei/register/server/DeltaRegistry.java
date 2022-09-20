package com.mobei.register.server;

import com.mobei.register.server.ServiceRegistry.RecentlyChangedServiceInstance;

import java.util.LinkedList;

/**
 * 增量注册表
 *
 * @author liuyaowu
 * @date 2022/9/16 7:29
 * @remark
 */
public class DeltaRegistry {

    private LinkedList<RecentlyChangedServiceInstance> recentlyChangedQueue;
    private Long serviceInstanceTotalCount;

    public DeltaRegistry(LinkedList<RecentlyChangedServiceInstance> recentlyChangedQueue,
                         Long serviceInstanceTotalCount) {
        this.recentlyChangedQueue = recentlyChangedQueue;
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }

    public LinkedList<RecentlyChangedServiceInstance> getRecentlyChangedQueue() {
        return recentlyChangedQueue;
    }

    public void setRecentlyChangedQueue(LinkedList<RecentlyChangedServiceInstance> recentlyChangedQueue) {
        this.recentlyChangedQueue = recentlyChangedQueue;
    }

    public Long getServiceInstanceTotalCount() {
        return serviceInstanceTotalCount;
    }

    public void setServiceInstanceTotalCount(Long serviceInstanceTotalCount) {
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }

}
