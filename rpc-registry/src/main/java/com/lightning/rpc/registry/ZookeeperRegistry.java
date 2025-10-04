package com.lightning.rpc.registry;

import com.lightning.rpc.core.Registry;
import com.lightning.rpc.core.ServiceChangeListener;
import com.lightning.rpc.core.ServiceInfo;
import com.lightning.rpc.core.RpcException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


public class ZookeeperRegistry implements Registry {
    
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);
    
    private final String connectString;
    private final int sessionTimeout;
    private ZooKeeper zooKeeper;
    private final Map<String, ServiceChangeListener> listeners = new ConcurrentHashMap<>();
    private final Map<String, List<ServiceInfo>> serviceCache = new ConcurrentHashMap<>();
    
    public ZookeeperRegistry(String connectString) {
        this(connectString, 30000);
    }
    
    public ZookeeperRegistry(String connectString, int sessionTimeout) {
        this.connectString = connectString;
        this.sessionTimeout = sessionTimeout;
    }
    
    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        if (zooKeeper == null) {
            connect();
        }
        
        String servicePath = "/lightning-rpc/" + serviceInfo.getServiceName();
        String nodePath = servicePath + "/" + serviceInfo.getServiceAddress();
        
        // 创建服务节点
        createPersistentNode(servicePath);
        
        // 创建临时节点
        String nodeData = serializeServiceInfo(serviceInfo);
        zooKeeper.create(nodePath, nodeData.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        
        logger.info("Service registered: {}", nodePath);
    }
    
    @Override
    public void unregister(ServiceInfo serviceInfo) throws Exception {
        if (zooKeeper == null) {
            return;
        }
        
        String servicePath = "/lightning-rpc/" + serviceInfo.getServiceName();
        String nodePath = servicePath + "/" + serviceInfo.getServiceAddress();
        
        // 删除节点
        zooKeeper.delete(nodePath, -1);
        
        logger.info("Service unregistered: {}", nodePath);
    }
    
    @Override
    public void subscribe(String serviceName, ServiceChangeListener listener) throws Exception {
        if (zooKeeper == null) {
            connect();
        }
        
        listeners.put(serviceName, listener);
        
        String servicePath = "/lightning-rpc/" + serviceName;
        
        // 监听服务节点变化
        zooKeeper.getChildren(servicePath, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
                        // 重新获取服务列表
                        List<ServiceInfo> services = getServices(serviceName);
                        listener.onServiceChange(serviceName, services);
                        
                        // 重新设置监听
                        zooKeeper.getChildren(servicePath, this);
                    } catch (Exception e) {
                        logger.error("Failed to handle service change", e);
                    }
                }
            }
        });
        
        logger.info("Subscribed to service: {}", serviceName);
    }
    
    @Override
    public void unsubscribe(String serviceName, ServiceChangeListener listener) throws Exception {
        listeners.remove(serviceName);
        logger.info("Unsubscribed from service: {}", serviceName);
    }
    
    @Override
    public List<ServiceInfo> getServices(String serviceName) throws Exception {
        return getServices(serviceName, null, null);
    }
    
    @Override
    public List<ServiceInfo> getServices(String serviceName, String version, String group) throws Exception {
        if (zooKeeper == null) {
            connect();
        }
        
        String servicePath = "/lightning-rpc/" + serviceName;
        List<ServiceInfo> services = new ArrayList<>();
        
        try {
            List<String> children = zooKeeper.getChildren(servicePath, false);
            
            for (String child : children) {
                String nodePath = servicePath + "/" + child;
                byte[] data = zooKeeper.getData(nodePath, false, null);
                
                if (data != null) {
                    ServiceInfo serviceInfo = deserializeServiceInfo(data);
                    
                    // 过滤版本和分�?
                    if (version != null && !version.equals(serviceInfo.getVersion())) {
                        continue;
                    }
                    if (group != null && !group.equals(serviceInfo.getGroup())) {
                        continue;
                    }
                    
                    services.add(serviceInfo);
                }
            }
            
        } catch (KeeperException.NoNodeException e) {
            logger.warn("Service path not found: {}", servicePath);
        }
        
        // 更新缓存
        serviceCache.put(serviceName, services);
        
        return services;
    }
    
    @Override
    public boolean isAvailable() {
        return zooKeeper != null && zooKeeper.getState() == ZooKeeper.States.CONNECTED;
    }
    
    @Override
    public void close() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                logger.error("Failed to close ZooKeeper", e);
                Thread.currentThread().interrupt();
            }
        }
    }
    
    
    private void connect() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            }
        });
        
        latch.await();
        logger.info("Connected to ZooKeeper: {}", connectString);
    }
    
    
    private void createPersistentNode(String path) throws Exception {
        String[] parts = path.split("/");
        String currentPath = "";
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            
            currentPath += "/" + part;
            
            try {
                zooKeeper.create(currentPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (KeeperException.NodeExistsException e) {
                // 节点已存在，忽略
            }
        }
    }
    
    
    private String serializeServiceInfo(ServiceInfo serviceInfo) {
        // 简单的序列化实现，实际项目中可以使用JSON或其他序列化方式
        return serviceInfo.getServiceName() + ":" + 
               serviceInfo.getHost() + ":" + 
               serviceInfo.getPort() + ":" + 
               serviceInfo.getVersion() + ":" + 
               serviceInfo.getGroup();
    }
    
    
    private ServiceInfo deserializeServiceInfo(byte[] data) {
        String[] parts = new String(data).split(":");
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceName(parts[0]);
        serviceInfo.setHost(parts[1]);
        serviceInfo.setPort(Integer.parseInt(parts[2]));
        serviceInfo.setVersion(parts[3]);
        serviceInfo.setGroup(parts[4]);
        return serviceInfo;
    }
}
