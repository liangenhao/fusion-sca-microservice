package io.fusion.common.generic;

import io.fusion.api.model.ProductAttribute;
import io.fusion.common.generic.converter.AttributeConverterRegistry;
import io.fusion.common.generic.converter.AttributeTypeConverter;
import io.fusion.common.generic.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author enhao
 */
public class AttributeValueManager {

    private final AttributeConverterRegistry converterRegistry;

    public AttributeValueManager(AttributeConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public <T> AttributeValue<T> convert(String typeName, String attrValue) {
        AttributeTypeConverter<T> converter = converterRegistry.getConverter(typeName);
        if (converter == null) {
            throw new IllegalArgumentException("No converter found for type: " + typeName);
        }

        T value = converter.convertToJavaType(attrValue);

        return new AttributeValue<>("", value, converter.getJavaType(), "");
    }

    public <T> String getTypeName(Class<T> cls, T value) {
        String typeName = cls.getName();
        if (List.class.isAssignableFrom(cls)) {
            List<?> valueOfList = (List<?>) value;
            if (valueOfList != null && !valueOfList.isEmpty()) {
                Class<?> elementType = valueOfList.get(0).getClass();
                typeName = getListTypeName(elementType);
            }
        }

        return typeName;
    }

    public <E> String getListTypeName(Class<E> elementType) {
        return "java.util.List<" + elementType.getName() + ">";
    }

    public static void main(String[] args) {
        System.out.println(List.class);
        System.out.println((Class<List<ProductAttribute>>)(Class<?>)List.class);
        System.out.println();

        AttributeValue<List<ProductAttribute>> listAttribute = AttributeValue.createListAttribute("", new ArrayList<>(), ProductAttribute.class, "");
    }


}
