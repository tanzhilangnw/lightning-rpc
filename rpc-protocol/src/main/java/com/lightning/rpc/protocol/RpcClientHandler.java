package com.lightning.rpc.protocol;

import com.lightning.rpc.core.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class RpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);
    
    private final Map<String, CompletableFuture<RpcResponse>> pendingRequests;
    
    public RpcClientHandler(Map<String, CompletableFuture<RpcResponse>> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        try {
            // 处理心跳消息
            if (msg.getHeader().getMessageType() == MessageType.HEARTBEAT) {
                handleHeartbeat(ctx, msg);
                return;
            }
            
            // 处理响应消息
            if (msg.getHeader().getMessageType() == MessageType.RESPONSE) {
                handleResponse(ctx, msg);
            }
            
        } catch (Exception e) {
            logger.error("Failed to handle RPC message", e);
        }
    }
    
    
    private void handleHeartbeat(ChannelHandlerContext ctx, RpcMessage msg) {
        logger.debug("Received heartbeat from {}", ctx.channel().remoteAddress());
        // 可以发送心跳响应或直接忽略
    }
    
    
    private void handleResponse(ChannelHandlerContext ctx, RpcMessage msg) {
        // 这里需要反序列化响应对�?
        // 为了简化，假设body已经是RpcResponse对象
        RpcResponse response = (RpcResponse) msg.getBody();
        
        // 获取对应的Future并完�?
        CompletableFuture<RpcResponse> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            if (response.isSuccess()) {
                future.complete(response);
            } else {
                future.completeExceptionally(response.getException());
            }
        } else {
            logger.warn("No pending request found for response: {}", response.getRequestId());
        }
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                // 发送心�?
                RpcMessage heartbeat = RpcMessage.createHeartbeat();
                ctx.writeAndFlush(heartbeat);
                logger.debug("Sent heartbeat to {}", ctx.channel().remoteAddress());
            } else if (event.state() == IdleState.READER_IDLE) {
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
        logger.info("Connected to server: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Disconnected from server: {}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
}
