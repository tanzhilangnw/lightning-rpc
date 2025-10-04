package com.lightning.rpc.registry;

import com.lightning.rpc.core.Registry;
import com.lightning.rpc.core.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;


public class RegistryFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistryFactory.class);
    
    
    private static final Map<String, Registry> REGISTRY_CACHE = new ConcurrentHashMap<>();
    
    
    public static Registry getRegistry(String type, String address) {
        String key = type + ":" + address;
        Registry registry = REGISTRY_CACHE.get(key);
        
        if (registry == null) {
            synchronized (RegistryFactory.class) {
                registry = REGISTRY_CACHE.get(key);
                if (registry == null) {
                    registry = createRegistry(type, address);
                    REGISTRY_CACHE.put(key, registry);
                }
            }
        }
        
        return registry;
    }
    
    
    private static Registry createRegistry(String type, String address) {
        switch (type.toLowerCase()) {
            case "zookeeper":
            case "zk":
                return new ZookeeperRegistry(address);
            case "nacos":
                return new NacosRegistry(address);
            default:
                throw new RpcException("Unsupported registry type: " + type);
        }
    }
    
    
    public static void removeRegistry(String type, String address) {
        String key = type + ":" + address;
        Registry registry = REGISTRY_CACHE.remove(key);
        if (registry != null) {
            registry.close();
            logger.info("Removed registry: {}", key);
        }
    }
    
    
    public static void closeAll() {
        for (Registry registry : REGISTRY_CACHE.values()) {
            try {
                registry.close();
            } catch (Exception e) {
                logger.error("Failed to close registry", e);
            }
        }
        REGISTRY_CACHE.clear();
        logger.info("All registries closed");
    }
}
