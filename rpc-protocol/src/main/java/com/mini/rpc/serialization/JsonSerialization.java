/*
 * Copyright (c) 2011-2018, Meituan Dianping. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mini.rpc.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JsonSerialization implements RpcSerialization {

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = generateMapper(JsonInclude.Include.ALWAYS);
  }

  private static ObjectMapper generateMapper(JsonInclude.Include include) {
    ObjectMapper customMapper = new ObjectMapper();
    //customMapper.setSerializationInclusion(include);
    //customMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    //customMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
    //customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    customMapper.registerModule(createJavaTimeModule());
    return customMapper;
  }

  public static JavaTimeModule createJavaTimeModule() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
    return javaTimeModule;
  }

  @Override
  public <T> byte[] serialize(T obj) throws IOException {
    return obj instanceof String ? ((String) obj).getBytes() : objectMapper.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
    return objectMapper.readValue(new String(data), clz);
  }

  @Override
  public <T> T deserialize(String string, Type type) throws IOException {
    TypeFactory typeFactory = objectMapper.getTypeFactory();
    JavaType javaType = typeFactory.constructType(type);
    return objectMapper.readValue(string, javaType);
  }

  @Override
  public Object[] deserialize(Object[] content, Type[] type) throws IOException {
    Object[] objects = new Object[content.length];
    for (int i = 0; i < content.length; i++) {
      objects[i] = deserialize(content[i].toString(), type[i]);
    }
    return objects;
  }

  @Override
  public String[] serializationString(Object[] args) {
    String[] json = new String[args.length];
    for (int i = 0; i < args.length; i++) {
      json[i] = serializationString(args[i]);
    }
    return json;
  }

  @Override
  public String serializationString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
