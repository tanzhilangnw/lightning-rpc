package com.lightning.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class RpcDecoder extends ByteToMessageDecoder {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcDecoder.class);
    
    
    private static final int HEADER_LENGTH = 16;
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的数据读取消息头
        if (in.readableBytes() < HEADER_LENGTH) {
            return;
        }
        
        // 标记读取位置
        in.markReaderIndex();
        
        try {
            // 读取消息�?
            RpcHeader header = readHeader(in);
            
            // 检查是否有足够的数据读取消息体
            if (in.readableBytes() < header.getBodyLength()) {
                // 重置读取位置，等待更多数�?
                in.resetReaderIndex();
                return;
            }
            
            // 读取消息�?
            byte[] bodyBytes = new byte[header.getBodyLength()];
            in.readBytes(bodyBytes);
            
            // 创建RPC消息
            RpcMessage message = new RpcMessage(header, bodyBytes);
            out.add(message);
            
        } catch (Exception e) {
            logger.error("Failed to decode RPC message", e);
            throw e;
        }
    }
    
    
    private RpcHeader readHeader(ByteBuf in) {
        RpcHeader header = new RpcHeader();
        
        // 读取魔数
        header.setMagic(in.readShort());
        
        // 读取版本
        header.setVersion(in.readByte());
        
        // 读取消息类型
        header.setMessageType(MessageType.fromValue(in.readByte()));
        
        // 读取序列化类�?
        header.setSerializerType(in.readByte());
        
        // 读取压缩类型
        header.setCompressType(in.readByte());
        
        // 跳过保留字节
        in.skipBytes(3);
        
        // 读取请求ID长度
        int requestIdLength = in.readInt();
        
        // 读取请求ID
        if (requestIdLength > 0) {
            byte[] requestIdBytes = new byte[requestIdLength];
            in.readBytes(requestIdBytes);
            header.setRequestId(new String(requestIdBytes));
        }
        
        // 读取消息体长�?
        header.setBodyLength(in.readInt());
        
        return header;
    }
}
