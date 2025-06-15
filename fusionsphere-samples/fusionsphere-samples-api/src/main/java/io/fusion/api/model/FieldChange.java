package io.fusion.api.model;

import io.fusion.api.annotation.CompareField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 字段变更记录
 *
 * @author enhao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldChange implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 类名.字段名
     */
    private String field;

    /**
     * 字段描述名
     */
    private String fieldAlias;

    /**
     * 旧值
     */
    private Object oldValue;

    /**
     * 旧值展示值
     */
    private String oldDisplayValue;

    /**
     * 新值
     */
    private Object newValue;

    /**
     * 新值展示值
     */
    private String newDisplayValue;

    /**
     * 比较字段注解
     */
    private CompareField compareField;
}
