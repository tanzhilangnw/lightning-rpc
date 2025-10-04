package com.lightning.rpc.core;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * CGLIB代理工厂
 * 基于CGLIB实现RPC客户端代理
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public class CglibProxyFactory implements ProxyFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(CglibProxyFactory.class);
    
    @Override
    public <T> T createProxy(Class<T> serviceInterface, Invoker invoker) {
        return createProxy(serviceInterface, invoker, null, null);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> serviceInterface, Invoker invoker, String version, String group) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceInterface);
        enhancer.setCallback(new RpcMethodInterceptor(invoker, version, group));
        
        return (T) enhancer.create();
    }
    
    /**
     * RPC方法拦截器
     */
    private static class RpcMethodInterceptor implements MethodInterceptor {
        
        private final Invoker invoker;
        private final String version;
        private final String group;
        
        public RpcMethodInterceptor(Invoker invoker, String version, String group) {
            this.invoker = invoker;
            this.version = version;
            this.group = group;
        }
        
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            // 跳过Object类的方法
            if (Object.class.equals(method.getDeclaringClass())) {
                return proxy.invokeSuper(obj, args);
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