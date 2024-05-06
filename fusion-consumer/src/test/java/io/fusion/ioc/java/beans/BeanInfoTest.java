package io.fusion.ioc.java.beans;

import org.junit.jupiter.api.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyEditorSupport;
import java.util.stream.Stream;

/**
 * @author enhao
 */
public class BeanInfoTest {

    @Test
    public void testPropertyDescriptor() throws IntrospectionException {
        // 第二个参数stopClass表示排出的类：
        // 例如：Object 中的 getClass 会被当作property ，所以需要排除
        // 例如：只需要当前类的 BeanInfo，可以排出父类的
        BeanInfo beanInfo = Introspector.getBeanInfo(Person.class, Object.class);
        Stream.of(beanInfo.getPropertyDescriptors())
                .forEach(System.out::println);
    }

    @Test
    public void testPropertyType() throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(Person.class, Object.class);
        Stream.of(beanInfo.getPropertyDescriptors())
                .forEach(propertyDescriptor -> {
                    // PropertyDescriptor 允许添加属性编辑器 - PropertyEditor
                    // 例如：GUI 中输入的是文本字符串，需要转换为 property 对应的类型（PropertyType）
                    Class<?> propertyType = propertyDescriptor.getPropertyType();
                    String propertyName = propertyDescriptor.getName();
                    if ("age".equals(propertyName)) {
                        // 输入 String -> 输出 Integer
                        propertyDescriptor.setPropertyEditorClass(StringToIntegerPropertyEditor.class);
                        // propertyDescriptor.createPropertyEditor();
                    }
                });
    }

    private static class StringToIntegerPropertyEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            Integer value = Integer.valueOf(text);
            setValue(value);
        }
    }
}
