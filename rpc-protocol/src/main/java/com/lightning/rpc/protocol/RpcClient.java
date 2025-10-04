package com.lightning.rpc.protocol;

import com.lightning.rpc.core.RpcConfig;
import com.lightning.rpc.core.RpcException;
import com.lightning.rpc.core.RpcRequest;
import com.lightning.rpc.core.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class RpcClient {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    
    private final RpcConfig config;
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<RpcResponse>> pendingRequests = new ConcurrentHashMap<>();
    
    private EventLoopGroup group;
    private boolean started = false;
    
    public RpcClient(RpcConfig config) {
        this.config = config;
    }
    
    
    public void start() {
        if (started) {
            logger.warn("RPC client is already started");
            return;
        }
        
        try {
            group = new NioEventLoopGroup();
            started = true;
            logger.info("RPC client started");
            
        } catch (Exception e) {
            logger.error("Failed to start RPC client", e);
            throw new RpcException("Failed to start RPC client", e);
        }
    }
    
    
    public void stop() {
        if (!started) {
            logger.warn("RPC client is not started");
            return;
        }
        
        try {
            // 关闭所有连�?
            for (Channel channel : channelMap.values()) {
                if (channel.isActive()) {
                    channel.close().sync();
                }
            }
            channelMap.clear();
            
            // 关闭事件循环�?
            if (group != null) {
                group.shutdownGracefully();
            }
            
            started = false;
            logger.info("RPC client stopped");
            
        } catch (Exception e) {
            logger.error("Failed to stop RPC client", e);
            throw new RpcException("Failed to stop RPC client", e);
        }
    }
    
    
    public RpcResponse sendRequest(RpcRequest request, String host, int port) throws Exception {
        Channel channel = getChannel(host, port);
        if (channel == null || !channel.isActive()) {
            throw new RpcException(RpcException.ErrorCode.NETWORK_ERROR, "Channel is not active");
        }
        
        // 创建请求消息
        RpcMessage requestMsg = RpcMessage.createRequest(request);
        
        // 创建响应Future
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        pendingRequests.put(request.getRequestId(), future);
        
        try {
            // 发送请�?
            channel.writeAndFlush(requestMsg);
            
            // 等待响应
            return future.get(config.getReadTimeout(), TimeUnit.MILLISECONDS);
            
        } catch (Exception e) {
            pendingRequests.remove(request.getRequestId());
            throw new RpcException(RpcException.ErrorCode.TIMEOUT, "Request timeout", e);
        }
    }
    
    
    public CompletableFuture<RpcResponse> sendRequestAsync(RpcRequest request, String host, int port) {
        Channel channel = getChannel(host, port);
        if (channel == null || !channel.isActive()) {
            CompletableFuture<RpcResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new RpcException(RpcException.ErrorCode.NETWORK_ERROR, "Channel is not active"));
            return future;
        }
        
        // 创建请求消息
        RpcMessage requestMsg = RpcMessage.createRequest(request);
        
        // 创建响应Future
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        pendingRequests.put(request.getRequestId(), future);
        
        try {
            // 发送请�?
            channel.writeAndFlush(requestMsg);
            return future;
            
        } catch (Exception e) {
            pendingRequests.remove(request.getRequestId());
            future.completeExceptionally(e);
            return future;
        }
    }
    
    
    private Channel getChannel(String host, int port) {
        String key = host + ":" + port;
        Channel channel = channelMap.get(key);
        
        if (channel == null || !channel.isActive()) {
            try {
                channel = createChannel(host, port);
                channelMap.put(key, channel);
            } catch (Exception e) {
                logger.error("Failed to create channel to {}:{}", host, port, e);
                return null;
            }
        }
        
        return channel;
    }
    
    
    private Channel createChannel(String host, int port) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeout())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        // 添加空闲状态处理器
                        pipeline.addLast(new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
                        
                        // 添加编解码器
                        pipeline.addLast(new RpcDecoder());
                        pipeline.addLast(new RpcEncoder());
                        
                        // 添加业务处理�?
                        pipeline.addLast(new RpcClientHandler(pendingRequests));
                    }
                });
        
        ChannelFuture future = bootstrap.connect(host, port).sync();
        return future.channel();
    }
    
    
    public void handleResponse(RpcResponse response) {
        CompletableFuture<RpcResponse> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            if (response.isSuccess()) {
                future.complete(response);
            } else {
                future.completeExceptionally(new RpcException(response.getException()));
            }
        }
    }
    
    
    public boolean isStarted() {
        return started;
    }
}
