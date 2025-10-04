package com.lightning.rpc.serialization;

import com.lightning.rpc.core.Serializer;
import com.lightning.rpc.core.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;


public class SerializerFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(SerializerFactory.class);
    
    
    private static final Map<SerializerType, Serializer> SERIALIZER_CACHE = new ConcurrentHashMap<>();
    
    static {
        // 初始化默认序列化�?
        registerSerializer(SerializerType.JSON, new JsonSerializer());
        registerSerializer(SerializerType.KRYO, new KryoSerializer());
        registerSerializer(SerializerType.PROTOBUF, new ProtobufSerializer());
    }
    
    
    public static Serializer getSerializer(SerializerType type) {
        Serializer serializer = SERIALIZER_CACHE.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unsupported serializer type: " + type);
        }
        return serializer;
    }
    
    
    public static void registerSerializer(SerializerType type, Serializer serializer) {
        SERIALIZER_CACHE.put(type, serializer);
        logger.info("Registered serializer: {}", type);
    }
    
    
    public static void removeSerializer(SerializerType type) {
        SERIALIZER_CACHE.remove(type);
        logger.info("Removed serializer: {}", type);
    }
    
    
    public static SerializerType[] getSupportedTypes() {
        return SERIALIZER_CACHE.keySet().toArray(new SerializerType[0]);
    }
    
    
    public static boolean isSupported(SerializerType type) {
        return SERIALIZER_CACHE.containsKey(type);
    }
}
