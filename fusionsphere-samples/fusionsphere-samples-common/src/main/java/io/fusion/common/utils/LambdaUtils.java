package io.fusion.common.utils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * Lambda工具类
 *
 * @author enhao
 */
public class LambdaUtils {

    /**
     * 获取字段名
     *
     * @param function Lambda表达式
     * @param <T>      实体类
     * @param <R>      字段值
     * @return 字段名
     */
    public static <T, R> String getFieldName(FieldFunction<T, R> function) {
        try {
            // 获取Lambda表达式的方法引用
            Method writeReplace = function.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(function);

            // 获取方法名
            String fieldName = getFieldName(serializedLambda);

            return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
        } catch (Exception e) {
            throw new RuntimeException("获取字段名失败", e);
        }
    }

    private static String getFieldName(SerializedLambda serializedLambda) {
        String implMethodName = serializedLambda.getImplMethodName();

        String fieldName = null;

        if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
            fieldName = implMethodName.substring(3);
        } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
            fieldName = implMethodName.substring(2);
        }
        if (fieldName == null) {
            throw new IllegalArgumentException("无法从方法 " + implMethodName + " 中提取字段名");
        }
        return fieldName;
    }
}