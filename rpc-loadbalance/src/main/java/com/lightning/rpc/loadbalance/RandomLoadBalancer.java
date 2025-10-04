package com.lightning.rpc.loadbalance;

import com.lightning.rpc.core.LoadBalancer;
import com.lightning.rpc.core.RpcRequest;
import com.lightning.rpc.core.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;


public class RandomLoadBalancer implements LoadBalancer {
    
    private static final Logger logger = LoggerFactory.getLogger(RandomLoadBalancer.class);
    
    private final Random random = new Random();
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services) {
        if (services == null || services.isEmpty()) {
            logger.warn("No services available for load balancing");
            return null;
        }
        
        int index = random.nextInt(services.size());
        ServiceInfo selected = services.get(index);
        
        logger.debug("Selected service: {} (random index: {})", selected.getServiceAddress(), index);
        return selected;
    }
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services, RpcRequest request) {
        return select(services);
    }
    
    @Override
    public String getName() {
        return "random";
    }
}
