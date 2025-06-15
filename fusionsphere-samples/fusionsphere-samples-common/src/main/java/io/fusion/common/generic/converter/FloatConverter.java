package io.fusion.common.generic.converter;

public class FloatConverter implements AttributeTypeConverter<Float> {
    
    @Override
    public Float convertToJavaType(String value) {
        return Float.parseFloat(value);
    }
    
    @Override
    public String convertToDatabaseValue(Float value) {
        return value != null ? value.toString() : null;
    }
    
    @Override
    public Class<Float> getJavaType() {
        return Float.class;
    }
}