package com.lightning.rpc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 代理工厂管理器
 * 负责管理不同类型的代理工厂
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public class ProxyFactoryManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ProxyFactoryManager.class);
    
    /**
     * 代理工厂缓存
     */
    private static final Map<String, ProxyFactory> PROXY_FACTORY_CACHE = new ConcurrentHashMap<>();
    
    static {
        // 注册默认代理工厂
        registerProxyFactory("jdk", new JdkProxyFactory());
        registerProxyFactory("cglib", new CglibProxyFactory());
    }
    
    /**
     * 获取代理工厂
     * 
     * @param type 代理工厂类型
     * @return 代理工厂实例
     */
    public static ProxyFactory getProxyFactory(String type) {
        ProxyFactory factory = PROXY_FACTORY_CACHE.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported proxy factory type: " + type);
        }
        return factory;
    }
    
    /**
     * 注册代理工厂
     * 
     * @param type 代理工厂类型
     * @param factory 代理工厂实例
     */
    public static void registerProxyFactory(String type, ProxyFactory factory) {
        PROXY_FACTORY_CACHE.put(type, factory);
        logger.info("Registered proxy factory: {}", type);
    }
    
    /**
     * 移除代理工厂
     * 
     * @param type 代理工厂类型
     */
    public static void removeProxyFactory(String type) {
        PROXY_FACTORY_CACHE.remove(type);
        logger.info("Removed proxy factory: {}", type);
    }
    
    /**
     * 获取所有支持的代理工厂类型
     * 
     * @return 代理工厂类型数组
     */
    public static String[] getSupportedTypes() {
        return PROXY_FACTORY_CACHE.keySet().toArray(new String[0]);
    }
    
    /**
     * 判断是否支持指定的代理工厂类型
     * 
     * @param type 代理工厂类型
     * @return 是否支持
     */
    public static boolean isSupported(String type) {
        return PROXY_FACTORY_CACHE.containsKey(type);
    }
}