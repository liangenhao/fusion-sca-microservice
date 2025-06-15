package io.fusion.spring.generic;

import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;

import java.util.Arrays;

/**
 * @author enhao
 */
public class ResolvableTypeDemo {

    static class Fruit<T> {

    }

    @Test
    public void testForMethod1() {
        ResolvableType resolvableType = ResolvableType.forClass(ResolvableTypeDemo.class);
        printResolvableType(resolvableType, ResolvableTypeDemo.class, "forClass");
        System.out.println("===============================");

        resolvableType = ResolvableType.forClass(Fruit.class);
        printResolvableType(resolvableType, Fruit.class, "forClass");
    }

    private void printResolvableType(ResolvableType resolvableType, Class<?> clazz, String factoryMethodName) {
        System.out.printf("%s(%s.class)#getComponentType : %s\n", factoryMethodName, clazz.getSimpleName(), resolvableType.getComponentType());
        System.out.printf("%s(%s.class)#getGenerics : %s\n", factoryMethodName, clazz.getSimpleName(), Arrays.toString(resolvableType.getGenerics()));
        System.out.printf("%s(%s.class)#getInterfaces : %s\n", factoryMethodName, clazz.getSimpleName(), Arrays.toString(resolvableType.getInterfaces()));
        System.out.printf("%s(%s.class)#getRawClass : %s\n", factoryMethodName, clazz.getSimpleName(), resolvableType.getRawClass());
        System.out.printf("%s(%s.class)#getSuperType : %s\n", factoryMethodName, clazz.getSimpleName(), resolvableType.getSuperType());
        System.out.printf("%s(%s.class)#getType : %s\n", factoryMethodName, clazz.getSimpleName(), resolvableType.getType());
        System.out.printf("%s(%s.class)#hasGenerics : %s\n", factoryMethodName, clazz.getSimpleName(), resolvableType.hasGenerics());

        // System.out.println("forClass(ResolvableType.class)#getComponentType : " + resolvableType.getComponentType());
        // System.out.println("forClass(ResolvableType.class)#getGenerics : " + Arrays.toString(resolvableType.getGenerics()));
        // System.out.println("forClass(ResolvableType.class)#getInterfaces : " + Arrays.toString(resolvableType.getInterfaces()));
        // System.out.println("forClass(ResolvableType.class)#getRawClass : " + resolvableType.getRawClass());
        // System.out.println("forClass(ResolvableType.class)#getSuperType : " + resolvableType.getSuperType());
        // System.out.println("forClass(ResolvableType.class)#getType : " + resolvableType.getType());
        // System.out.println("forClass(ResolvableType.class)#hasGenerics : " + resolvableType.hasGenerics());
    }

}
