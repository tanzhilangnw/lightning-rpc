package com.lightning.rpc.loadbalance;

import com.lightning.rpc.core.LoadBalancer;
import com.lightning.rpc.core.RpcRequest;
import com.lightning.rpc.core.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class RoundRobinLoadBalancer implements LoadBalancer {
    
    private static final Logger logger = LoggerFactory.getLogger(RoundRobinLoadBalancer.class);
    
    private final AtomicInteger index = new AtomicInteger(0);
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services) {
        if (services == null || services.isEmpty()) {
            logger.warn("No services available for load balancing");
            return null;
        }
        
        int currentIndex = index.getAndIncrement() % services.size();
        ServiceInfo selected = services.get(currentIndex);
        
        logger.debug("Selected service: {} (index: {})", selected.getServiceAddress(), currentIndex);
        return selected;
    }
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services, RpcRequest request) {
        return select(services);
    }
    
    @Override
    public String getName() {
        return "roundRobin";
    }
}
