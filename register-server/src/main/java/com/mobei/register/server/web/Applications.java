package com.mobei.register.server.web;

import com.mobei.register.server.core.ServiceInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * 完整的服务实例的信息
 *
 * @author liuyaowu
 * @date 2022/9/20 21:06
 * @remark
 */
public class Applications {

    private Map<String, Map<String, ServiceInstance>> registry = new HashMap<>();

    public Applications() {
    }

    public Applications(Map<String, Map<String, ServiceInstance>> registry) {
        this.registry = registry;
    }

    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    public void setRegistry(Map<String, Map<String, ServiceInstance>> registry) {
        this.registry = registry;
    }

}
