package com.lightning.rpc.core;

/**
 * 序列化器接口
 * 定义了序列化和反序列化的核心方法
 * 
 * @author tanzhilangnw
 * @since 1.0.0
 */
public interface Serializer {
    
    /**
     * 序列化
     * 
     * @param obj 待序列化对象
     * @return 序列化后的字节数组
     * @throws Exception 序列化异常
     */
    byte[] serialize(Object obj) throws Exception;
    
    /**
     * 反序列化
     * 
     * @param data 序列化数据
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 反序列化后的对象
     * @throws Exception 反序列化异常
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws Exception;
    
    /**
     * 获取序列化器类型
     * 
     * @return 序列化器类型
     */
    SerializerType getType();
}