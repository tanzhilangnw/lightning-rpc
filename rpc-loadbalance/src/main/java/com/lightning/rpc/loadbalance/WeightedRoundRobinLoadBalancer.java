package com.lightning.rpc.loadbalance;

import com.lightning.rpc.core.LoadBalancer;
import com.lightning.rpc.core.RpcRequest;
import com.lightning.rpc.core.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class WeightedRoundRobinLoadBalancer implements LoadBalancer {
    
    private static final Logger logger = LoggerFactory.getLogger(WeightedRoundRobinLoadBalancer.class);
    
    private final AtomicInteger currentWeight = new AtomicInteger(0);
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services) {
        if (services == null || services.isEmpty()) {
            logger.warn("No services available for load balancing");
            return null;
        }
        
        if (services.size() == 1) {
            return services.get(0);
        }
        
        int totalWeight = 0;
        for (ServiceInfo service : services) {
            totalWeight += service.getWeight();
        }
        
        if (totalWeight == 0) {
            // 如果所有权重都�?，退化为普通轮�?
            int index = currentIndex.getAndIncrement() % services.size();
            return services.get(index);
        }
        
        // 加权轮询算法
        while (true) {
            int current = currentIndex.get();
            ServiceInfo service = services.get(current);
            
            if (currentWeight.get() == 0) {
                currentWeight.set(service.getWeight());
            }
            
            if (currentWeight.get() > 0) {
                currentWeight.decrementAndGet();
                return service;
            }
            
            currentIndex.set((current + 1) % services.size());
            if (currentIndex.get() == 0) {
                // 重新计算权重
                for (ServiceInfo s : services) {
                    currentWeight.addAndGet(s.getWeight());
                }
            }
        }
    }
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services, RpcRequest request) {
        return select(services);
    }
    
    @Override
    public String getName() {
        return "weightedRoundRobin";
    }
}
