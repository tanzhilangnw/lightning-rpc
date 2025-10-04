package com.lightning.rpc.core;

import java.util.List;

/**
 * 服务变更监听器
 * 当服务列表发生变化时，会通知此监听器
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public interface ServiceChangeListener {
    
    /**
     * 服务变更通知
     * 
     * @param serviceName 服务名称
     * @param services 新的服务列表
     */
    void onServiceChange(String serviceName, List<ServiceInfo> services);
}