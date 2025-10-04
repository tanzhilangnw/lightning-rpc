package com.lightning.rpc.core;

import java.util.concurrent.CompletableFuture;

/**
 * 调用器接口
 * 定义了RPC调用的核心方法
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public interface Invoker {
    
    /**
     * 同步调用
     * 
     * @param request RPC请求
     * @return RPC响应
     * @throws Exception 调用异常
     */
    RpcResponse invoke(RpcRequest request) throws Exception;
    
    /**
     * 异步调用
     * 
     * @param request RPC请求
     * @return 异步响应
     */
    CompletableFuture<RpcResponse> invokeAsync(RpcRequest request);
    
    /**
     * 获取服务信息
     * 
     * @return 服务信息
     */
    ServiceInfo getServiceInfo();
    
    /**
     * 判断调用器是否可用
     * 
     * @return 是否可用
     */
    boolean isAvailable();
    
    /**
     * 销毁调用器
     */
    void destroy();
}