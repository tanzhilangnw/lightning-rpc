package com.lightning.rpc.example;

import com.lightning.rpc.core.RpcConfig;
import com.lightning.rpc.core.RpcRequest;
import com.lightning.rpc.core.RpcResponse;
import com.lightning.rpc.protocol.RpcClient;
import com.lightning.rpc.example.model.User;
import com.lightning.rpc.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcClientExample {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcClientExample.class);
    
    public static void main(String[] args) {
        try {
            // 创建RPC配置
            RpcConfig config = new RpcConfig();
            config.setApplicationName("user-client");
            config.setSerializerType(com.lightning.rpc.core.SerializerType.JSON);
            config.setConnectTimeout(3000);
            config.setReadTimeout(5000);
            
            // 创建RPC客户�?
            RpcClient client = new RpcClient(config);
            client.start();
            
            // 测试用户服务调用
            testUserService(client);
            
            // 关闭客户�?
            client.stop();
            
        } catch (Exception e) {
            logger.error("RPC client test failed", e);
        }
    }
    
    
    private static void testUserService(RpcClient client) throws Exception {
        logger.info("Testing UserService...");
        
        // 测试创建用户
        User user = new User(null, "张三", "zhangsan@example.com", 25);
        RpcRequest createRequest = new RpcRequest();
        createRequest.setRequestId("create-user-001");
        createRequest.setInterfaceName("com.lightning.rpc.example.service.UserService");
        createRequest.setMethodName("createUser");
        createRequest.setParameterTypes(new Class[]{User.class});
        createRequest.setParameters(new Object[]{user});
        createRequest.setVersion("1.0.0");
        createRequest.setGroup("default");
        
        RpcResponse createResponse = client.sendRequest(createRequest, "127.0.0.1", 8080);
        logger.info("Create user response: {}", createResponse);
        
        // 测试获取用户
        RpcRequest getRequest = new RpcRequest();
        getRequest.setRequestId("get-user-001");
        getRequest.setInterfaceName("com.lightning.rpc.example.service.UserService");
        getRequest.setMethodName("getUserById");
        getRequest.setParameterTypes(new Class[]{Long.class});
        getRequest.setParameters(new Object[]{1L});
        getRequest.setVersion("1.0.0");
        getRequest.setGroup("default");
        
        RpcResponse getResponse = client.sendRequest(getRequest, "127.0.0.1", 8080);
        logger.info("Get user response: {}", getResponse);
        
        if (getResponse.isSuccess()) {
            User result = (User) getResponse.getResult();
            logger.info("Retrieved user: {}", result);
        }
    }
}
