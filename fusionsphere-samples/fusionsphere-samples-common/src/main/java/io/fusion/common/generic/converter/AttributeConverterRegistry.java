package io.fusion.common.generic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AttributeConverterRegistry {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<String, AttributeTypeConverter<?>> converterMap = new ConcurrentHashMap<>();

    private static final String PREFIX_OF_LIST_TYPE = "java.util.List<";
    private static final String SUFFIX_OF_LIST_TYPE = ">";

    @PostConstruct
    public void init() {
        // 注册基本数据类型转换器
        registerConverter(Integer.class.getName(), new IntegerConverter());
        registerConverter(Float.class.getName(), new FloatConverter());
        registerConverter(String.class.getName(), new StringConverter());

        // 可以在这里注册更多的基本类型转换器
    }

    /**
     * 注册转换器
     *
     * @param typeName  类型名称
     * @param converter 转换器实例
     */
    public void registerConverter(String typeName, AttributeTypeConverter<?> converter) {
        converterMap.put(typeName, converter);
    }

    /**
     * 获取转换器
     *
     * @param typeName 类型名称
     * @return 对应的转换器
     */
    @SuppressWarnings("unchecked")
    public <T> AttributeTypeConverter<T> getConverter(String typeName) {
        AttributeTypeConverter<?> converter = converterMap.get(typeName);

        if (converter == null) {
            // 尝试动态创建JSON对象转换器
            try {
                // 检查是否为List类型（处理JSON数组）
                if (typeName.startsWith(PREFIX_OF_LIST_TYPE) && typeName.endsWith(SUFFIX_OF_LIST_TYPE)) {
                    String elementTypeName = typeName.substring(PREFIX_OF_LIST_TYPE.length(), typeName.length() - 1);
                    Class<?> elementType = Class.forName(elementTypeName);
                    converter = createJsonArrayConverter(elementType);
                } else {
                    // 创建JSON对象转换器
                    Class<?> clazz = Class.forName(typeName);
                    converter = createJsonObjectConverter(clazz);
                }

                // 注册新创建的转换器
                registerConverter(typeName, converter);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unknown attribute type: " + typeName, e);
            }
        }

        return (AttributeTypeConverter<T>) converter;
    }

    /**
     * 创建JSON对象转换器
     *
     * @param type 目标Java类型
     * @return JSON对象转换器
     */
    @SuppressWarnings("unchecked")
    private <T> AttributeTypeConverter<T> createJsonObjectConverter(Class<?> type) {
        return new JsonObjectConverter<>((Class<T>) type);
    }

    /**
     * 创建JSON数组转换器
     *
     * @param elementType 数组元素的Java类型
     * @return JSON数组转换器
     */
    @SuppressWarnings("unchecked")
    private <T> AttributeTypeConverter<List<T>> createJsonArrayConverter(Class<?> elementType) {
        return new JsonArrayConverter<>((Class<T>) elementType);
    }
}