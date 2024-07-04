package io.fusion.framework.spring.boot.datasource.annotation;

import io.fusion.framework.spring.boot.datasource.properties.DynamicDataSourceProperties;

import java.lang.annotation.*;

/**
 * 切换数据源注解
 *
 * @author enhao
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {

    /**
     * 目标数据源名称
     *
     * @return 数据源名称
     * @see DynamicDataSourceProperties#getDatasource()
     */
    String value() default "master";
}
