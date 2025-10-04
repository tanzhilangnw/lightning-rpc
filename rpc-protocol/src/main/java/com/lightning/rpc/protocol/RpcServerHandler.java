package com.lightning.rpc.protocol;

import com.lightning.rpc.core.RpcRequest;
import com.lightning.rpc.core.RpcResponse;
import com.lightning.rpc.core.ServiceInfo;
import com.lightning.rpc.core.RpcException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;


public class RpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);
    
    private final Map<String, ServiceInfo> serviceMap;
    
    public RpcServerHandler(Map<String, ServiceInfo> serviceMap) {
        this.serviceMap = serviceMap;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        try {
            // 处理心跳消息
            if (msg.getHeader().getMessageType() == MessageType.HEARTBEAT) {
                handleHeartbeat(ctx, msg);
                return;
            }
            
            // 处理请求消息
            if (msg.getHeader().getMessageType() == MessageType.REQUEST) {
                handleRequest(ctx, msg);
            }
            
        } catch (Exception e) {
            logger.error("Failed to handle RPC message", e);
            // 发送错误响�?
            RpcResponse response = new RpcResponse(msg.getHeader().getRequestId(), e);
            RpcMessage responseMsg = RpcMessage.createResponse(response);
            ctx.writeAndFlush(responseMsg);
        }
    }
    
    
    private void handleHeartbeat(ChannelHandlerContext ctx, RpcMessage msg) {
        logger.debug("Received heartbeat from {}", ctx.channel().remoteAddress());
        // 可以返回心跳响应或直接忽�?
    }
    
    
    private void handleRequest(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        // 这里需要反序列化请求对�?
        // 为了简化，假设body已经是RpcRequest对象
        RpcRequest request = (RpcRequest) msg.getBody();
        
        // 执行方法调用
        RpcResponse response = invokeMethod(request);
        
        // 发送响�?
        RpcMessage responseMsg = RpcMessage.createResponse(response);
        ctx.writeAndFlush(responseMsg);
    }
    
    
    private RpcResponse invokeMethod(RpcRequest request) {
        try {
            // 构建服务�?
            String serviceKey = request.getInterfaceName() + ":" + request.getVersion() + ":" + request.getGroup();
            
            // 获取服务信息
            ServiceInfo serviceInfo = serviceMap.get(serviceKey);
            if (serviceInfo == null) {
                throw new RpcException(RpcException.ErrorCode.SERVICE_NOT_FOUND, 
                    "Service not found: " + serviceKey);
            }
            
            // 获取服务实例
            Object serviceInstance = serviceInfo.getServiceInstance();
            if (serviceInstance == null) {
                throw new RpcException(RpcException.ErrorCode.SERVICE_NOT_FOUND, 
                    "Service instance not found: " + serviceKey);
            }
            
            // 获取方法
            Method method = serviceInstance.getClass().getMethod(
                request.getMethodName(), request.getParameterTypes());
            
            // 执行方法调用
            Object result = method.invoke(serviceInstance, request.getParameters());
            
            // 返回成功响应
            return new RpcResponse(request.getRequestId(), result);
            
        } catch (Exception e) {
            logger.error("Failed to invoke method", e);
            return new RpcResponse(request.getRequestId(), e);
        }
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.warn("Channel {} read timeout, closing connection", ctx.channel().remoteAddress());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Channel {} exception", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client connected: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client disconnected: {}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
}
