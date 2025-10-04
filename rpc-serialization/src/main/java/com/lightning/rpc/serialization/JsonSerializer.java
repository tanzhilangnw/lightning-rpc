package com.lightning.rpc.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightning.rpc.core.Serializer;
import com.lightning.rpc.core.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonSerializer implements Serializer {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    
    private final ObjectMapper objectMapper;
    
    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
        // 配置ObjectMapper
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    @Override
    public byte[] serialize(Object obj) throws Exception {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object", e);
            throw new Exception("Serialization failed", e);
        }
    }
    
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (Exception e) {
            logger.error("Failed to deserialize data", e);
            throw new Exception("Deserialization failed", e);
        }
    }
    
    @Override
    public SerializerType getType() {
        return SerializerType.JSON;
    }
}
