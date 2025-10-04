package com.lightning.rpc.registry;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lightning.rpc.core.Registry;
import com.lightning.rpc.core.ServiceChangeListener;
import com.lightning.rpc.core.ServiceInfo;
import com.lightning.rpc.core.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public class NacosRegistry implements Registry {
    
    private static final Logger logger = LoggerFactory.getLogger(NacosRegistry.class);
    
    private final String serverAddr;
    private final String namespace;
    private NamingService namingService;
    private final Map<String, ServiceChangeListener> listeners = new ConcurrentHashMap<>();
    
    public NacosRegistry(String serverAddr) {
        this(serverAddr, null);
    }
    
    public NacosRegistry(String serverAddr, String namespace) {
        this.serverAddr = serverAddr;
        this.namespace = namespace;
    }
    
    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        if (namingService == null) {
            connect();
        }
        
        Instance instance = new Instance();
        instance.setIp(serviceInfo.getHost());
        instance.setPort(serviceInfo.getPort());
        instance.setWeight(serviceInfo.getWeight());
        instance.setHealthy(true);
        
        // 设置元数�?
        instance.getMetadata().put("version", serviceInfo.getVersion());
        instance.getMetadata().put("group", serviceInfo.getGroup());
        
        namingService.registerInstance(serviceInfo.getServiceName(), instance);
        
        logger.info("Service registered: {}:{}", serviceInfo.getServiceName(), serviceInfo.getServiceAddress());
    }
    
    @Override
    public void unregister(ServiceInfo serviceInfo) throws Exception {
        if (namingService == null) {
            return;
        }
        
        Instance instance = new Instance();
        instance.setIp(serviceInfo.getHost());
        instance.setPort(serviceInfo.getPort());
        
        namingService.deregisterInstance(serviceInfo.getServiceName(), instance);
        
        logger.info("Service unregistered: {}:{}", serviceInfo.getServiceName(), serviceInfo.getServiceAddress());
    }
    
    @Override
    public void subscribe(String serviceName, ServiceChangeListener listener) throws Exception {
        if (namingService == null) {
            connect();
        }
        
        listeners.put(serviceName, listener);
        
        // 订阅服务变化
        namingService.subscribe(serviceName, event -> {
            try {
                List<ServiceInfo> services = getServices(serviceName);
                listener.onServiceChange(serviceName, services);
            } catch (Exception e) {
                logger.error("Failed to handle service change", e);
            }
        });
        
        logger.info("Subscribed to service: {}", serviceName);
    }
    
    @Override
    public void unsubscribe(String serviceName, ServiceChangeListener listener) throws Exception {
        if (namingService == null) {
            return;
        }
        
        namingService.unsubscribe(serviceName, event -> {});
        listeners.remove(serviceName);
        
        logger.info("Unsubscribed from service: {}", serviceName);
    }
    
    @Override
    public List<ServiceInfo> getServices(String serviceName) throws Exception {
        return getServices(serviceName, null, null);
    }
    
    @Override
    public List<ServiceInfo> getServices(String serviceName, String version, String group) throws Exception {
        if (namingService == null) {
            connect();
        }
        
        List<Instance> instances = namingService.getAllInstances(serviceName);
        List<ServiceInfo> services = new ArrayList<>();
        
        for (Instance instance : instances) {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceName(serviceName);
            serviceInfo.setHost(instance.getIp());
            serviceInfo.setPort(instance.getPort());
            serviceInfo.setWeight(instance.getWeight());
            
            // 从元数据获取版本和分�?
            String instanceVersion = instance.getMetadata().get("version");
            String instanceGroup = instance.getMetadata().get("group");
            serviceInfo.setVersion(instanceVersion);
            serviceInfo.setGroup(instanceGroup);
            
            // 过滤版本和分�?
            if (version != null && !version.equals(instanceVersion)) {
                continue;
            }
            if (group != null && !group.equals(instanceGroup)) {
                continue;
            }
            
            services.add(serviceInfo);
        }
        
        return services;
    }
    
    @Override
    public boolean isAvailable() {
        return namingService != null;
    }
    
    @Override
    public void close() {
        if (namingService != null) {
            try {
                namingService.shutDown();
            } catch (NacosException e) {
                logger.error("Failed to close Nacos", e);
            }
        }
    }
    
    
    private void connect() throws Exception {
        try {
            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);
            if (namespace != null) {
                properties.put("namespace", namespace);
            }
            
            namingService = NacosFactory.createNamingService(properties);
            logger.info("Connected to Nacos: {}", serverAddr);
            
        } catch (NacosException e) {
            logger.error("Failed to connect to Nacos", e);
            throw new RpcException("Failed to connect to Nacos", e);
        }
    }
}
