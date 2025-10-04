package com.lightning.rpc.core;

/**
 * 代理工厂接口
 * 用于创建RPC客户端代理对象
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public interface ProxyFactory {
    
    /**
     * 创建代理对象
     * 
     * @param serviceInterface 服务接口
     * @param invoker 调用器
     * @param <T> 服务接口类型
     * @return 代理对象
     */
    <T> T createProxy(Class<T> serviceInterface, Invoker invoker);
    
    /**
     * 创建代理对象（带版本和分组）
     * 
     * @param serviceInterface 服务接口
     * @param invoker 调用器
     * @param version 服务版本
     * @param group 服务分组
     * @param <T> 服务接口类型
     * @return 代理对象
     */
    <T> T createProxy(Class<T> serviceInterface, Invoker invoker, String version, String group);
}