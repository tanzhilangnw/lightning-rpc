package com.lightning.rpc.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lightning.rpc.core.Serializer;
import com.lightning.rpc.core.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class KryoSerializer implements Serializer {
    
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    
    
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            // 配置Kryo
            kryo.setRegistrationRequired(false);
            kryo.setReferences(true);
            return kryo;
        }
    };
    
    @Override
    public byte[] serialize(Object obj) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Output output = new Output(outputStream)) {
            
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            kryo.writeObject(output, obj);
            output.flush();
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Failed to serialize object with Kryo", e);
            throw new Exception("Kryo serialization failed", e);
        }
    }
    
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             Input input = new Input(inputStream)) {
            
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            return kryo.readObject(input, clazz);
            
        } catch (Exception e) {
            logger.error("Failed to deserialize data with Kryo", e);
            throw new Exception("Kryo deserialization failed", e);
        }
    }
    
    @Override
    public SerializerType getType() {
        return SerializerType.KRYO;
    }
}
