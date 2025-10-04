package com.lightning.rpc.loadbalance;

import com.lightning.rpc.core.LoadBalancer;
import com.lightning.rpc.core.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;


public class LoadBalancerFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerFactory.class);
    
    
    private static final Map<String, LoadBalancer> LOAD_BALANCER_CACHE = new ConcurrentHashMap<>();
    
    static {
        // 注册默认负载均衡�?
        registerLoadBalancer("roundRobin", new RoundRobinLoadBalancer());
        registerLoadBalancer("random", new RandomLoadBalancer());
        registerLoadBalancer("weightedRoundRobin", new WeightedRoundRobinLoadBalancer());
        registerLoadBalancer("consistentHash", new ConsistentHashLoadBalancer());
    }
    
    
    public static LoadBalancer getLoadBalancer(String name) {
        LoadBalancer loadBalancer = LOAD_BALANCER_CACHE.get(name);
        if (loadBalancer == null) {
            throw new RpcException("Unsupported load balancer: " + name);
        }
        return loadBalancer;
    }
    
    
    public static void registerLoadBalancer(String name, LoadBalancer loadBalancer) {
        LOAD_BALANCER_CACHE.put(name, loadBalancer);
        logger.info("Registered load balancer: {}", name);
    }
    
    
    public static void removeLoadBalancer(String name) {
        LOAD_BALANCER_CACHE.remove(name);
        logger.info("Removed load balancer: {}", name);
    }
    
    
    public static String[] getSupportedLoadBalancers() {
        return LOAD_BALANCER_CACHE.keySet().toArray(new String[0]);
    }
    
    
    public static boolean isSupported(String name) {
        return LOAD_BALANCER_CACHE.containsKey(name);
    }
}
