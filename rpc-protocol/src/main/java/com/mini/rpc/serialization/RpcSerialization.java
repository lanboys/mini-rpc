package com.mini.rpc.serialization;

import java.io.IOException;
import java.lang.reflect.Type;

public interface RpcSerialization {
    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;

    <T> T deserialize(String str, Type type) throws IOException;

    Object[] deserialize(Object[] str, Type[] type) throws IOException;

    String[] serializationString(Object[] objects);

    String serializationString(Object object);
}