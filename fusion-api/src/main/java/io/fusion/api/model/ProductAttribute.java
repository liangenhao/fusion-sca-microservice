package io.fusion.api.model;

import lombok.Data;

@Data
public class ProductAttribute {
    private Long id;
    private String productId;
    private String attrCode;
    private String attrValue;
    private String attrType;
    private String attrDesc;
}