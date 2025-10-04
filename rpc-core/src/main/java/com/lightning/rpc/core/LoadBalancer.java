package com.lightning.rpc.core;

import java.util.List;

/**
 * 负载均衡器接口
 * 定义了负载均衡算法的核心方法
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public interface LoadBalancer {
    
    /**
     * 选择服务
     * 
     * @param services 服务列表
     * @return 选中的服务
     */
    ServiceInfo select(List<ServiceInfo> services);
    
    /**
     * 选择服务（带请求参数）
     * 
     * @param services 服务列表
     * @param request RPC请求
     * @return 选中的服务
     */
    ServiceInfo select(List<ServiceInfo> services, RpcRequest request);
    
    /**
     * 获取负载均衡算法名称
     * 
     * @return 算法名称
     */
    String getName();
}