package com.lightning.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcEncoder extends MessageToByteEncoder<RpcMessage> {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);
    
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        try {
            RpcHeader header = msg.getHeader();
            
            // 写入魔数
            out.writeShort(header.getMagic());
            
            // 写入版本
            out.writeByte(header.getVersion());
            
            // 写入消息类型
            out.writeByte(header.getMessageType().getValue());
            
            // 写入序列化类�?
            out.writeByte(header.getSerializerType());
            
            // 写入压缩类型
            out.writeByte(header.getCompressType());
            
            // 写入保留字节
            out.writeBytes(new byte[3]);
            
            // 写入请求ID
            String requestId = header.getRequestId();
            if (requestId != null) {
                byte[] requestIdBytes = requestId.getBytes();
                out.writeInt(requestIdBytes.length);
                out.writeBytes(requestIdBytes);
            } else {
                out.writeInt(0);
            }
            
            // 写入消息体长�?
            byte[] bodyBytes = (byte[]) msg.getBody();
            out.writeInt(bodyBytes.length);
            
            // 写入消息�?
            out.writeBytes(bodyBytes);
            
        } catch (Exception e) {
            logger.error("Failed to encode RPC message", e);
            throw e;
        }
    }
}
