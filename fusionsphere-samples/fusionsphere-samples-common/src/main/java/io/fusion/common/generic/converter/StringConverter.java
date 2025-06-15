package io.fusion.common.generic.converter;

public class StringConverter implements AttributeTypeConverter<String> {
    
    @Override
    public String convertToJavaType(String value) {
        return value;
    }
    
    @Override
    public String convertToDatabaseValue(String value) {
        return value;
    }
    
    @Override
    public Class<String> getJavaType() {
        return String.class;
    }
}