package com.lightning.rpc.loadbalance;

import com.lightning.rpc.core.LoadBalancer;
import com.lightning.rpc.core.RpcRequest;
import com.lightning.rpc.core.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class ConsistentHashLoadBalancer implements LoadBalancer {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsistentHashLoadBalancer.class);
    
    private final SortedMap<Integer, ServiceInfo> circle = new TreeMap<>();
    private final int virtualNodes;
    
    public ConsistentHashLoadBalancer() {
        this(160); // 默认160个虚拟节�?
    }
    
    public ConsistentHashLoadBalancer(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services) {
        if (services == null || services.isEmpty()) {
            logger.warn("No services available for load balancing");
            return null;
        }
        
        // 构建哈希�?
        buildHashCircle(services);
        
        // 使用请求ID作为哈希�?
        String key = "default";
        return getServiceForKey(key);
    }
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services, RpcRequest request) {
        if (services == null || services.isEmpty()) {
            logger.warn("No services available for load balancing");
            return null;
        }
        
        // 构建哈希�?
        buildHashCircle(services);
        
        // 使用请求ID作为哈希�?
        String key = request.getRequestId();
        return getServiceForKey(key);
    }
    
    
    private void buildHashCircle(List<ServiceInfo> services) {
        circle.clear();
        
        for (ServiceInfo service : services) {
            // 为每个服务添加虚拟节�?
            for (int i = 0; i < virtualNodes; i++) {
                String virtualNodeName = service.getServiceAddress() + "#" + i;
                int hash = hash(virtualNodeName);
                circle.put(hash, service);
            }
        }
        
        logger.debug("Built hash circle with {} virtual nodes", circle.size());
    }
    
    
    private ServiceInfo getServiceForKey(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        
        int hash = hash(key);
        SortedMap<Integer, ServiceInfo> tailMap = circle.tailMap(hash);
        
        if (tailMap.isEmpty()) {
            // 如果没有找到，返回第一个节点（环形结构�?
            return circle.get(circle.firstKey());
        }
        
        return tailMap.get(tailMap.firstKey());
    }
    
    
    private int hash(String key) {
        return key.hashCode();
    }
    
    @Override
    public String getName() {
        return "consistentHash";
    }
}
