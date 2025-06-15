package io.fusion.java.ioc.generic;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

/**
 * @author enhao
 */
public class GenericApiTest {

    static class MyClass<T> {
        private List<String> list1;
        private List<T> list2;
        private List list3;
    }

    @Test
    public void testParameterizedType() throws NoSuchFieldException {
        Field list1Field = MyClass.class.getDeclaredField("list1");
        Type list1GenericType = list1Field.getGenericType();
        if (list1GenericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) list1GenericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            // 这里的 Type 是 Class 类型
            Type typeArgument = typeArguments[0];
        }

        Field list2Field = MyClass.class.getDeclaredField("list2");
        Type list2GenericType = list2Field.getGenericType();
        if (list2GenericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) list2GenericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            // 这里的 Type 是 TypeVariable 类型
            Type typeArgument = typeArguments[0];
        }

        Field list3Field = MyClass.class.getDeclaredField("list3");
        Type list3GenericType = list3Field.getGenericType();
        // 这里的 type 是 Class， 不是 ParameterizedType
        if (list3GenericType instanceof ParameterizedType) {
            System.out.println(list3GenericType);
        }
    }

    static class MyArrayClass<T> {
        private T[] tArray;
        private List<String>[] listArray;
        private String[] stringArray;
    }

    @Test
    public void testGenericArrayType1() throws NoSuchFieldException {
        Field field = MyArrayClass.class.getDeclaredField("tArray");
        Type genericType = field.getGenericType();
        if (genericType instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) genericType;
            Type genericComponentType = (genericArrayType).getGenericComponentType();
            if (genericComponentType instanceof TypeVariable) {
                System.out.println(genericComponentType);
            }
        }
    }

    @Test
    public void testGenericArrayType2() throws NoSuchFieldException {
        Field field = MyArrayClass.class.getDeclaredField("listArray");
        Type genericType = field.getGenericType();
        if (genericType instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) genericType;
            Type genericComponentType = (genericArrayType).getGenericComponentType();
            if (genericComponentType instanceof ParameterizedType) {
                Type[] typeArguments = ((ParameterizedType) genericComponentType).getActualTypeArguments();
                Type typeArgument = typeArguments[0];
                System.out.println(typeArgument); // class java.lang.String
            }
        }
    }

    @Test
    public void testGenericArrayType3() throws NoSuchFieldException {
        Field field = MyArrayClass.class.getDeclaredField("stringArray");
        Type genericType = field.getGenericType();
        if (genericType instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) genericType;
            Type genericComponentType = (genericArrayType).getGenericComponentType();
        }
    }

    static class MyTypeClass<T> {
        private List<T> list;

        private <E> E getElement(T value) {
            return null;
        }

        public MyTypeClass(T list) {
        }
    }

    @Test
    public void testTypeVariable() throws NoSuchFieldException, NoSuchMethodException {
        // 通过类获取
        TypeVariable<Class<MyTypeClass>>[] typeParameters = MyTypeClass.class.getTypeParameters();
        for (TypeVariable<Class<MyTypeClass>> typeParameter : typeParameters) {
            System.out.println(typeParameter);
        }

        // 通过字段获取
        Field field = MyTypeClass.class.getDeclaredField("list");
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            Type typeArgument = typeArguments[0];
            if (typeArgument instanceof TypeVariable) {
                System.out.println(typeArgument);
            }
        }

        // 通过方法获取
        Method method = MyTypeClass.class.getDeclaredMethod("getElement", Object.class);
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (Type genericParameterType : genericParameterTypes) {
            if (genericParameterType instanceof TypeVariable) {
                GenericDeclaration genericDeclaration = ((TypeVariable<?>) genericParameterType).getGenericDeclaration();
                System.out.println("getElement() genericParameterType-GenericDeclaration: " + genericDeclaration);
            }
        }
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof TypeVariable) {
            GenericDeclaration genericDeclaration = ((TypeVariable<?>) genericReturnType).getGenericDeclaration();
            System.out.println("getElement() genericReturnType-GenericDeclaration:" + genericDeclaration);
        }
        // 构造方法
        Constructor<MyTypeClass> constructor = MyTypeClass.class.getDeclaredConstructor(Object.class);
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        Type[] genericExceptionTypes = constructor.getGenericExceptionTypes();
        System.out.println(Arrays.toString(parameterTypes));
    }

}
