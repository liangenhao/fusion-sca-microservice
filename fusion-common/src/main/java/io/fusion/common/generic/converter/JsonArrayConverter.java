package io.fusion.common.generic.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

/**
 * JSON数组转换器
 * @param <T> 集合中元素的Java类型
 */
public class JsonArrayConverter<T> implements AttributeTypeConverter<List<T>> {
    
    private final Class<T> elementType;
    private final ObjectMapper objectMapper;
    
    public JsonArrayConverter(Class<T> elementType) {
        this.elementType = elementType;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public List<T> convertToJavaType(String value) {
        try {
            return objectMapper.readValue(value,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON array to List<" + elementType.getName() + ">", e);
        }
    }
    
    @Override
    public String convertToDatabaseValue(List<T> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert List<" + elementType.getName() + "> to JSON array", e);
        }
    }
    
    @Override
    public Class<List<T>> getJavaType() {
        @SuppressWarnings("unchecked")
        Class<List<T>> listClass = (Class<List<T>>) (Class<?>) List.class;
        return listClass;
    }
}