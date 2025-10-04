package com.lightning.rpc.protocol;

import com.lightning.rpc.core.RpcConfig;
import com.lightning.rpc.core.RpcException;
import com.lightning.rpc.core.ServiceInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class RpcServer {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    
    private final RpcConfig config;
    private final Map<String, ServiceInfo> serviceMap = new ConcurrentHashMap<>();
    
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private boolean started = false;
    
    public RpcServer(RpcConfig config) {
        this.config = config;
    }
    
    
    public void start() {
        if (started) {
            logger.warn("RPC server is already started");
            return;
        }
        
        try {
            // 创建事件循环�?
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(config.getThreadPoolSize());
            
            // 创建服务器启动器
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            
                            // 添加空闲状态处理器
                            pipeline.addLast(new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
                            
                            // 添加编解码器
                            pipeline.addLast(new RpcDecoder());
                            pipeline.addLast(new RpcEncoder());
                            
                            // 添加业务处理�?
                            pipeline.addLast(new RpcServerHandler(serviceMap));
                        }
                    });
            
            // 绑定端口
            ChannelFuture future = bootstrap.bind(config.getHost(), config.getPort()).sync();
            serverChannel = future.channel();
            started = true;
            
            logger.info("RPC server started on {}:{}", config.getHost(), config.getPort());
            
        } catch (Exception e) {
            logger.error("Failed to start RPC server", e);
            throw new RpcException("Failed to start RPC server", e);
        }
    }
    
    
    public void stop() {
        if (!started) {
            logger.warn("RPC server is not started");
            return;
        }
        
        try {
            if (serverChannel != null) {
                serverChannel.close().sync();
            }
            
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            
            started = false;
            logger.info("RPC server stopped");
            
        } catch (Exception e) {
            logger.error("Failed to stop RPC server", e);
            throw new RpcException("Failed to stop RPC server", e);
        }
    }
    
    
    public void registerService(ServiceInfo serviceInfo) {
        String serviceKey = serviceInfo.getServiceKey();
        serviceMap.put(serviceKey, serviceInfo);
        logger.info("Service registered: {}", serviceKey);
    }
    
    
    public void unregisterService(ServiceInfo serviceInfo) {
        String serviceKey = serviceInfo.getServiceKey();
        serviceMap.remove(serviceKey);
        logger.info("Service unregistered: {}", serviceKey);
    }
    
    
    public boolean isStarted() {
        return started;
    }
    
    
    public ServiceInfo getService(String serviceKey) {
        return serviceMap.get(serviceKey);
    }
}
