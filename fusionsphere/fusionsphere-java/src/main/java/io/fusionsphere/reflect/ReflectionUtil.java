package io.fusionsphere.reflect;

import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * Java 反射工具类
 *
 * @author enhao
 */
public class ReflectionUtil {

    private ReflectionUtil() {
    }

    /**
     * 尝试在提供的类上查找具有提供的名称和参数类型的 Method。搜索所有超类，直到 Object。
     * <p>
     * 如果查找不到，尝试将参数类型拆包为原始类型，或将原始类型装包成包装类型后重新查找
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param paramTypes 方法参数类型
     * @return 方法
     */
    public static Method findMethod(Class<?> clazz, String methodName, @Nullable Class<?> paramTypes) {
        Method method = ReflectionUtils.findMethod(clazz, methodName, paramTypes);
        if (method == null && paramTypes != null && paramTypes.isPrimitive()) {
            method = ReflectionUtils.findMethod(clazz, methodName, getWrapperType(paramTypes));
        } else if (method == null && paramTypes != null) {
            method = ReflectionUtils.findMethod(clazz, methodName, getPrimitiveType(paramTypes));
        }
        return method;
    }

    private static Class<?> getPrimitiveType(Class<?> wrapperType) {
        if (wrapperType == Integer.class) return int.class;
        if (wrapperType == Long.class) return long.class;
        if (wrapperType == Boolean.class) return boolean.class;
        if (wrapperType == Double.class) return double.class;
        if (wrapperType == Float.class) return float.class;
        if (wrapperType == Character.class) return char.class;
        if (wrapperType == Byte.class) return byte.class;
        if (wrapperType == Short.class) return short.class;
        return wrapperType;
    }

    private static Class<?> getWrapperType(Class<?> primitiveType) {
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == double.class) return Double.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == short.class) return Short.class;
        return primitiveType;
    }
}
