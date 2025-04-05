package io.fusion.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author enhao
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CompareField {

    /**
     * 字段名称，必填
     */
    String name();

    /**
     * 指定枚举类，当字段为枚举值非枚举对象时指定
     */
    Class<?> enumClass() default Void.class;

    /**
     * 当指定 {@link #enumClass()} 时，指定用作枚举值的字段名，默认 {@code code}
     */
    String enumCodeField() default "code";

    /**
     * 当字段为枚举类型时，指定用作展示的字段名，可为空
     */
    String enumDisplayField() default "";

    /**
     * 是否递归比对嵌套对象，默认否
     */
    boolean nested() default false;

    /**
     * 递归深度，当 {@link #nested()} 为 {@code true} 时生效，默认 1
     */
    int maxDepth() default 1;

    /**
     * 时间格式化
     */
    String dateFormat() default "";

    /**
     * 是否为关键字段，默认否
     */
    boolean keyField() default false;

    /**
     * 是否在差异日志中忽略该字段，默认否
     */
    boolean ignore() default false;
}
