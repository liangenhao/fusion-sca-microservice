package io.fusion.common.generic.model;

import lombok.Data;

import java.util.List;

/**
 * 属性值包装类
 *
 * @param <T> 属性值的Java类型
 */
@Data
public class AttributeValue<T> {
    private String code;
    private T value;
    private Class<T> type;
    private String description;

    public AttributeValue(String code, T value, Class<T> type, String description) {
        this.code = code;
        this.value = value;
        this.type = type;
        this.description = description;
    }

    public static <E> AttributeValue<List<E>> createListAttribute(String code, List<E> value, Class<E> elementType, String description) {
        return new AttributeValue<>(code, value, (Class<List<E>>)(Class<?>) List.class, description);
    }
}