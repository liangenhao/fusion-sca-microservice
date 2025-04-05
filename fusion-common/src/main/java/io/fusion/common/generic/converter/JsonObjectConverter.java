package io.fusion.common.generic.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * JSON对象转换器
 * @param <T> 转换后的Java类型
 */
public class JsonObjectConverter<T> implements AttributeTypeConverter<T> {
    
    private final Class<T> type;
    private final ObjectMapper objectMapper;
    
    public JsonObjectConverter(Class<T> type) {
        this.type = type;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public T convertToJavaType(String value) {
        try {
            return objectMapper.readValue(value, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to " + type.getName(), e);
        }
    }
    
    @Override
    public String convertToDatabaseValue(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert " + type.getName() + " to JSON", e);
        }
    }
    
    @Override
    public Class<T> getJavaType() {
        return type;
    }
}