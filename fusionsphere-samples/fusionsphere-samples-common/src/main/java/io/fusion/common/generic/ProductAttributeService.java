package io.fusion.common.generic;

import io.fusion.api.model.ProductAttribute;
import io.fusion.common.generic.converter.AttributeConverterRegistry;
import io.fusion.common.generic.converter.AttributeTypeConverter;
import io.fusion.common.generic.model.AttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductAttributeService {
    
    // @Autowired
    // private ProductAttributeRepository repository;
    
    @Autowired
    private AttributeConverterRegistry converterRegistry;
    
    /**
     * 读取产品的所有属性，并转换为对应的Java类型
     * @param productId 产品ID
     * @return 转换后的属性值列表
     */
    public <T> List<AttributeValue<T>> getProductAttributes(String productId) {
        // List<ProductAttribute> attributes = repository.findByProductId(productId);
        List<ProductAttribute> attributes = new ArrayList<>();
        List<AttributeValue<T>> result = new ArrayList<>();
        
        for (ProductAttribute attribute : attributes) {
            AttributeValue<T> attributeValue = convertToAttributeValue(attribute);
            result.add(attributeValue);
        }
        
        return result;
    }
    
    /**
     * 将数据库属性转换为Java对象
     * @param attribute 数据库属性
     * @return 转换后的属性值
     */
    @SuppressWarnings("unchecked")
    private <T> AttributeValue<T> convertToAttributeValue(ProductAttribute attribute) {
        String typeName = attribute.getAttrType();
        AttributeTypeConverter<T> converter = converterRegistry.getConverter(typeName);
        
        T value = converter.convertToJavaType(attribute.getAttrValue());
        Class<T> type = converter.getJavaType();
        
        return new AttributeValue<>(
                attribute.getAttrCode(),
                value,
                type,
                attribute.getAttrDesc()
        );
    }
    
    /**
     * 保存产品属性
     * @param productId 产品ID
     * @param attributeValues 属性值列表
     */
    public <T> void saveProductAttributes(String productId, List<AttributeValue<T>> attributeValues) {
        List<ProductAttribute> attributes = new ArrayList<>();
        
        for (AttributeValue<T> attributeValue : attributeValues) {
            ProductAttribute attribute = convertToProductAttribute(productId, attributeValue);
            attributes.add(attribute);
        }
        
        // repository.saveAll(attributes);
    }
    
    /**
     * 将Java对象转换为数据库属性
     * @param productId 产品ID
     * @param attributeValue 属性值
     * @return 数据库属性
     */
    @SuppressWarnings("unchecked")
    private <T> ProductAttribute convertToProductAttribute(String productId, AttributeValue<T> attributeValue) {
        T value = attributeValue.getValue();
        Class<T> type = attributeValue.getType();
        
        // 获取完整的类型名称
        String typeName = type.getName();
        
        // 对于List类型，需要特殊处理以保留元素类型信息
        if (List.class.isAssignableFrom(type)) {
            List<?> valueOfList = (List<?>) value;
            if (valueOfList != null && !valueOfList.isEmpty()) {
                Object firstElement = valueOfList.get(0);
                typeName = "java.util.List<" + firstElement.getClass().getName() + ">";
            }
        }
        
        AttributeTypeConverter<T> converter = converterRegistry.getConverter(typeName);
        String dbValue = converter.convertToDatabaseValue(value);
        
        ProductAttribute attribute = new ProductAttribute();
        attribute.setProductId(productId);
        attribute.setAttrCode(attributeValue.getCode());
        attribute.setAttrValue(dbValue);
        attribute.setAttrType(typeName);
        attribute.setAttrDesc(attributeValue.getDescription());
        
        return attribute;
    }
}