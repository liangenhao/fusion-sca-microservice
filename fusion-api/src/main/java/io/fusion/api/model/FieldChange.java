package io.fusion.api.model;

import io.fusion.api.annotation.FieldCompare;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段变更记录
 *
 * @author enhao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldChange {

    private String fieldName;

    private Object oldValue;

    private Object newValue;

    private FieldCompare fieldCompare;
}
