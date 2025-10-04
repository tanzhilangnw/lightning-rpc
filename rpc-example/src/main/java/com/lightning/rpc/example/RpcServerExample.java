package com.lightning.rpc.example;

import com.lightning.rpc.core.RpcConfig;
import com.lightning.rpc.core.ServiceInfo;
import com.lightning.rpc.protocol.RpcServer;
import com.lightning.rpc.example.service.UserService;
import com.lightning.rpc.example.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcServerExample {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcServerExample.class);
    
    public static void main(String[] args) {
        try {
            // 创建RPC配置
            RpcConfig config = new RpcConfig();
            config.setApplicationName("user-service");
            config.setHost("127.0.0.1");
            config.setPort(8080);
            config.setSerializerType(com.lightning.rpc.core.SerializerType.JSON);
            
            // 创建RPC服务�?
            RpcServer server = new RpcServer(config);
            
            // 创建服务实例
            UserService userService = new UserServiceImpl();
            
            // 创建服务信息
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceName("com.lightning.rpc.example.service.UserService");
            serviceInfo.setServiceInterface(UserService.class);
            serviceInfo.setServiceInstance(userService);
            serviceInfo.setVersion("1.0.0");
            serviceInfo.setGroup("default");
            serviceInfo.setHost("127.0.0.1");
            serviceInfo.setPort(8080);
            serviceInfo.setWeight(100);
            
            // 注册服务
            server.registerService(serviceInfo);
            
            // 启动服务�?
            server.start();
            
            logger.info("RPC server started successfully on {}:{}", config.getHost(), config.getPort());
            logger.info("Service registered: {}", serviceInfo.getServiceName());
            
            // 保持服务器运�?
            Thread.currentThread().join();
            
        } catch (Exception e) {
            logger.error("Failed to start RPC server", e);
        }
    }
}
