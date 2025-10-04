package com.lightning.rpc.core;

import java.util.List;

/**
 * 注册中心接口
 * 定义了服务注册与发现的核心方法
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public interface Registry {
    
    /**
     * 注册服务
     * 
     * @param serviceInfo 服务信息
     * @throws Exception 注册异常
     */
    void register(ServiceInfo serviceInfo) throws Exception;
    
    /**
     * 注销服务
     * 
     * @param serviceInfo 服务信息
     * @throws Exception 注销异常
     */
    void unregister(ServiceInfo serviceInfo) throws Exception;
    
    /**
     * 订阅服务
     * 
     * @param serviceName 服务名称
     * @param listener 服务变更监听器
     * @throws Exception 订阅异常
     */
    void subscribe(String serviceName, ServiceChangeListener listener) throws Exception;
    
    /**
     * 取消订阅
     * 
     * @param serviceName 服务名称
     * @param listener 服务变更监听器
     * @throws Exception 取消订阅异常
     */
    void unsubscribe(String serviceName, ServiceChangeListener listener) throws Exception;
    
    /**
     * 获取服务列表
     * 
     * @param serviceName 服务名称
     * @return 服务信息列表
     * @throws Exception 获取异常
     */
    List<ServiceInfo> getServices(String serviceName) throws Exception;
    
    /**
     * 获取服务列表（带版本和分组）
     * 
     * @param serviceName 服务名称
     * @param version 服务版本
     * @param group 服务分组
     * @return 服务信息列表
     * @throws Exception 获取异常
     */
    List<ServiceInfo> getServices(String serviceName, String version, String group) throws Exception;
    
    /**
     * 判断注册中心是否可用
     * 
     * @return 是否可用
     */
    boolean isAvailable();
    
    /**
     * 关闭注册中心
     */
    void close();
}