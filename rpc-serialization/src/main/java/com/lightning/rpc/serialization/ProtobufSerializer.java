package com.lightning.rpc.serialization;

import com.google.protobuf.Message;
import com.lightning.rpc.core.Serializer;
import com.lightning.rpc.core.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProtobufSerializer implements Serializer {
    
    private static final Logger logger = LoggerFactory.getLogger(ProtobufSerializer.class);
    
    @Override
    public byte[] serialize(Object obj) throws Exception {
        try {
            if (obj instanceof Message) {
                return ((Message) obj).toByteArray();
            } else {
                throw new IllegalArgumentException("Object must be a protobuf Message");
            }
        } catch (Exception e) {
            logger.error("Failed to serialize object with Protobuf", e);
            throw new Exception("Protobuf serialization failed", e);
        }
    }
    
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
        try {
            if (Message.class.isAssignableFrom(clazz)) {
                // 这里需要根据具体的protobuf类来实现
                // 为了简化，这里只是示例
                throw new UnsupportedOperationException("Protobuf deserialization requires specific implementation");
            } else {
                throw new IllegalArgumentException("Class must be a protobuf Message");
            }
        } catch (Exception e) {
            logger.error("Failed to deserialize data with Protobuf", e);
            throw new Exception("Protobuf deserialization failed", e);
        }
    }
    
    @Override
    public SerializerType getType() {
        return SerializerType.PROTOBUF;
    }
}
