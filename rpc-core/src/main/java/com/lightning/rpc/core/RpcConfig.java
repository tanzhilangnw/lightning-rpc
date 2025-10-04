package com.lightning.rpc.core;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC配置类
 * 封装了RPC框架的所有配置参数
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public class RpcConfig {
    
    /**
     * 应用名称
     */
    private String applicationName = "lightning-rpc";
    
    /**
     * 服务端口
     */
    private int port = 8080;
    
    /**
     * 服务主机
     */
    private String host = "127.0.0.1";
    
    /**
     * 序列化器类型
     */
    private SerializerType serializerType = SerializerType.JSON;
    
    /**
     * 注册中心地址
     */
    private String registryAddress = "127.0.0.1:2181";
    
    /**
     * 注册中心类型
     */
    private String registryType = "zookeeper";
    
    /**
     * 负载均衡算法
     */
    private String loadBalancer = "roundRobin";
    
    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 3000;
    
    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 5000;
    
    /**
     * 最大连接数
     */
    private int maxConnections = 100;
    
    /**
     * 心跳间隔（毫秒）
     */
    private int heartbeatInterval = 30000;
    
    /**
     * 重试次数
     */
    private int retryTimes = 3;
    
    /**
     * 线程池大小
     */
    private int threadPoolSize = 10;
    
    /**
     * 扩展配置
     */
    private Map<String, Object> extensions = new HashMap<>();
    
    public RpcConfig() {}
    
    /**
     * 添加扩展配置
     * 
     * @param key 配置键
     * @param value 配置值
     */
    public void addExtension(String key, Object value) {
        this.extensions.put(key, value);
    }
    
    /**
     * 获取扩展配置
     * 
     * @param key 配置键
     * @return 配置值
     */
    public Object getExtension(String key) {
        return this.extensions.get(key);
    }
    
    // Getters and Setters
    public String getApplicationName() {
        return applicationName;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public SerializerType getSerializerType() {
        return serializerType;
    }
    
    public void setSerializerType(SerializerType serializerType) {
        this.serializerType = serializerType;
    }
    
    public String getRegistryAddress() {
        return registryAddress;
    }
    
    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }
    
    public String getRegistryType() {
        return registryType;
    }
    
    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }
    
    public String getLoadBalancer() {
        return loadBalancer;
    }
    
    public void setLoadBalancer(String loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
    
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }
    
    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
    
    public int getRetryTimes() {
        return retryTimes;
    }
    
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }
    
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
    
    public Map<String, Object> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
    
    @Override
    public String toString() {
        return "RpcConfig{" +
                "applicationName='" + applicationName + '\'' +
                ", port=" + port +
                ", host='" + host + '\'' +
                ", serializerType=" + serializerType +
                ", registryAddress='" + registryAddress + '\'' +
                ", registryType='" + registryType + '\'' +
                ", loadBalancer='" + loadBalancer + '\'' +
                ", connectTimeout=" + connectTimeout +
                ", readTimeout=" + readTimeout +
                ", maxConnections=" + maxConnections +
                ", heartbeatInterval=" + heartbeatInterval +
                ", retryTimes=" + retryTimes +
                ", threadPoolSize=" + threadPoolSize +
                '}';
    }
}