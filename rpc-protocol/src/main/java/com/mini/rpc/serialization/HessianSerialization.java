package com.mini.rpc.serialization;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HessianSerialization implements RpcSerialization {

    @Override
    public <T> byte[] serialize(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        byte[] results;

        HessianSerializerOutput hessianOutput;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            hessianOutput = new HessianSerializerOutput(os);
            hessianOutput.writeObject(object);
            hessianOutput.flush();
            results = os.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clz) {
        if (bytes == null) {
            throw new NullPointerException();
        }
        T result;

        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            HessianSerializerInput hessianInput = new HessianSerializerInput(is);
            result = (T) hessianInput.readObject(clz);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return result;
    }

    @Override
    public <T> T deserialize(String str, Type type) throws IOException {
        throw new RuntimeException("暂时不支持");
    }

    @Override
    public Object[] deserialize(Object[] str, Type[] type) throws IOException {
        throw new RuntimeException("暂时不支持");
    }

    @Override
    public String[] serializationString(Object[] objects) {
        throw new RuntimeException("暂时不支持");
    }

    @Override
    public String serializationString(Object object) {
        throw new RuntimeException("暂时不支持");
    }
}
