package io.fusion.common.generic.converter;

public class IntegerConverter implements AttributeTypeConverter<Integer> {
    
    @Override
    public Integer convertToJavaType(String value) {
        return Integer.parseInt(value);
    }
    
    @Override
    public String convertToDatabaseValue(Integer value) {
        return value != null ? value.toString() : null;
    }
    
    @Override
    public Class<Integer> getJavaType() {
        return Integer.class;
    }
}