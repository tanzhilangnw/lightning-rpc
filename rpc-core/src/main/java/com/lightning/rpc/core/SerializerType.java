package com.lightning.rpc.core;

/**
 * 序列化器类型枚举
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public enum SerializerType {
    
    /**
     * JSON序列化
     */
    JSON("json"),
    
    /**
     * Protobuf序列化
     */
    PROTOBUF("protobuf"),
    
    /**
     * Kryo序列化
     */
    KRYO("kryo"),
    
    /**
     * Hessian序列化
     */
    HESSIAN("hessian");
    
    private final String name;
    
    SerializerType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * 根据名称获取序列化器类型
     * 
     * @param name 名称
     * @return 序列化器类型
     */
    public static SerializerType fromName(String name) {
        for (SerializerType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown serializer type: " + name);
    }
}