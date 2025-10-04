package com.lightning.rpc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

/**
 * JDK动态代理工厂
 * 基于JDK动态代理实现RPC客户端代理
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public class JdkProxyFactory implements ProxyFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(JdkProxyFactory.class);
    
    @Override
    public <T> T createProxy(Class<T> serviceInterface, Invoker invoker) {
        return createProxy(serviceInterface, invoker, null, null);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> serviceInterface, Invoker invoker, String version, String group) {
        return (T) Proxy.newProxyInstance(
            serviceInterface.getClassLoader(),
            new Class[]{serviceInterface},
            new RpcInvocationHandler(invoker, version, group)
        );
    }
    
    /**
     * RPC调用处理器
     */
    private static class RpcInvocationHandler implements InvocationHandler {
        
        private final Invoker invoker;
        private final String version;
        private final String group;
        
        public RpcInvocationHandler(Invoker invoker, String version, String group) {
            this.invoker = invoker;
            this.version = version;
            this.group = group;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 跳过Object类的方法
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }
            
            // 构建RPC请求
            RpcRequest request = new RpcRequest();
            request.setRequestId(generateRequestId());
            request.setInterfaceName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setVersion(version);
            request.setGroup(group);
            
            try {
                // 同步调用
                RpcResponse response = invoker.invoke(request);
                
                if (response.isSuccess()) {
                    return response.getResult();
                } else {
                    throw new RpcException(response.getException());
                }
                
            } catch (Exception e) {
                logger.error("RPC call failed", e);
                throw new RpcException("RPC call failed", e);
            }
        }
        
        /**
         * 生成请求ID
         */
        private String generateRequestId() {
            return System.currentTimeMillis() + "-" + Thread.currentThread().getId();
        }
    }
}