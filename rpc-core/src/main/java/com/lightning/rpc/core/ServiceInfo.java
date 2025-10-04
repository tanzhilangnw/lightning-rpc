package com.lightning.rpc.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务信息
 * 封装了服务提供者的详细信息
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public class ServiceInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 服务接口
     */
    private Class<?> serviceInterface;
    
    /**
     * 服务实现类
     */
    private Class<?> serviceImpl;
    
    /**
     * 服务实例
     */
    private Object serviceInstance;
    
    /**
     * 服务版本
     */
    private String version;
    
    /**
     * 服务分组
     */
    private String group;
    
    /**
     * 服务权重
     */
    private int weight;
    
    /**
     * 服务地址
     */
    private String host;
    
    /**
     * 服务端口
     */
    private int port;
    
    /**
     * 服务元数据
     */
    private Map<String, Object> metadata;
    
    public ServiceInfo() {
        this.metadata = new ConcurrentHashMap<>();
    }
    
    public ServiceInfo(String serviceName, Class<?> serviceInterface, Object serviceInstance) {
        this();
        this.serviceName = serviceName;
        this.serviceInterface = serviceInterface;
        this.serviceInstance = serviceInstance;
    }
    
    /**
     * 获取服务唯一标识
     */
    public String getServiceKey() {
        return serviceName + ":" + version + ":" + group;
    }
    
    /**
     * 获取服务地址
     */
    public String getServiceAddress() {
        return host + ":" + port;
    }
    
    /**
     * 添加元数据
     */
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 获取元数据
     */
    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }
    
    // Getters and Setters
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public Class<?> getServiceInterface() {
        return serviceInterface;
    }
    
    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
    
    public Class<?> getServiceImpl() {
        return serviceImpl;
    }
    
    public void setServiceImpl(Class<?> serviceImpl) {
        this.serviceImpl = serviceImpl;
    }
    
    public Object getServiceInstance() {
        return serviceInstance;
    }
    
    public void setServiceInstance(Object serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "ServiceInfo{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceInterface=" + serviceInterface +
                ", version='" + version + '\'' +
                ", group='" + group + '\'' +
                ", weight=" + weight +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", metadata=" + metadata +
                '}';
    }
}