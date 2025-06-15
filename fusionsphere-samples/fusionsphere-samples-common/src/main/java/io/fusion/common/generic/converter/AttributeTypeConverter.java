package io.fusion.common.generic.converter;

/**
 * 属性类型转换器接口
 *
 * @param <T> 转换后的Java类型
 */
public interface AttributeTypeConverter<T> {

    /**
     * 将字符串值转换为指定的Java类型
     *
     * @param value 属性值字符串
     * @return 转换后的Java对象
     */
    T convertToJavaType(String value);

    /**
     * 将Java对象转换为字符串，用于存储到数据库
     *
     * @param value Java对象
     * @return 转换后的字符串
     */
    String convertToDatabaseValue(T value);

    /**
     * 获取此转换器支持的Java类型
     *
     * @return Java类型的Class对象
     */
    Class<T> getJavaType();
}