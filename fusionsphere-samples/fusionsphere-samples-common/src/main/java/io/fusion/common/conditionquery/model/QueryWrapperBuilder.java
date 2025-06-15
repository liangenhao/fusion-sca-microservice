package io.fusion.common.conditionquery.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.fusion.common.conditionquery.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QueryWrapperBuilder {
    
    public static <T> QueryWrapper<T> build(QueryParam queryParam, Class<T> entityClass) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        
        if (queryParam.getConditions() != null) {
            buildConditions(queryWrapper, queryParam.getConditions(), entityClass);
        }
        
        if (queryParam.getSorts() != null) {
            for (QueryParam.Sort sort : queryParam.getSorts()) {
                String column = getColumnName(sort.getField(), entityClass);
                if ("desc".equalsIgnoreCase(sort.getOrder())) {
                    queryWrapper.orderByDesc(column);
                } else {
                    queryWrapper.orderByAsc(column);
                }
            }
        }
        
        return queryWrapper;
    }
    
    private static <T> void buildConditions(QueryWrapper<T> queryWrapper, 
                                          List<QueryParam.Condition> conditions,
                                          Class<T> entityClass) {
        for (QueryParam.Condition condition : conditions) {
            if (condition.getConditions() != null && !condition.getConditions().isEmpty()) {
                // 处理嵌套条件
                if ("or".equalsIgnoreCase(condition.getLogic())) {
                    queryWrapper.or(qw -> {
                        for (QueryParam.Condition subCondition : condition.getConditions()) {
                            if (subCondition.getConditions() != null && !subCondition.getConditions().isEmpty()) {
                                // 处理嵌套子条件
                                buildConditions(qw, Collections.singletonList(subCondition), entityClass);
                            } else {
                                applyCondition(qw, subCondition, entityClass);
                            }
                        }
                    });
                } else {
                    queryWrapper.and(qw -> {
                        for (QueryParam.Condition subCondition : condition.getConditions()) {
                            if (subCondition.getConditions() != null && !subCondition.getConditions().isEmpty()) {
                                // 处理嵌套子条件
                                buildConditions(qw, Collections.singletonList(subCondition), entityClass);
                            } else {
                                applyCondition(qw, subCondition, entityClass);
                            }
                        }
                    });
                }
            } else {
                // 处理普通条件
                applyCondition(queryWrapper, condition, entityClass);
            }
        }
    }
    
    private static <T> void applyCondition(QueryWrapper<T> queryWrapper, 
                                         QueryParam.Condition condition, 
                                         Class<T> entityClass) {
        String column = getColumnName(condition.getField(), entityClass);
        String operator = condition.getOperator().toLowerCase();
        Object value = condition.getValue();
        
        switch (operator) {
            case "eq":
                queryWrapper.eq(column, value);
                break;
            case "ne":
                queryWrapper.ne(column, value);
                break;
            case "gt":
                queryWrapper.gt(column, value);
                break;
            case "ge":
                queryWrapper.ge(column, value);
                break;
            case "lt":
                queryWrapper.lt(column, value);
                break;
            case "le":
                queryWrapper.le(column, value);
                break;
            case "like":
                queryWrapper.like(column, value);
                break;
            case "notlike":
                queryWrapper.notLike(column, value);
                break;
            case "in":
                if (value instanceof Collection) {
                    queryWrapper.in(column, (Collection<?>) value);
                } else if (value.getClass().isArray()) {
                    queryWrapper.in(column, (Object[]) value);
                } else if (value instanceof String) {
                    queryWrapper.in(column, ((String) value).split(","));
                }
                break;
            case "notin":
                if (value instanceof Collection) {
                    queryWrapper.notIn(column, (Collection<?>) value);
                } else if (value.getClass().isArray()) {
                    queryWrapper.notIn(column, (Object[]) value);
                } else if (value instanceof String) {
                    queryWrapper.notIn(column, ((String) value).split(","));
                }
                break;
            case "isnull":
                queryWrapper.isNull(column);
                break;
            case "isnotnull":
                queryWrapper.isNotNull(column);
                break;
            case "between":
                if (value instanceof Object[] && ((Object[]) value).length == 2) {
                    Object[] range = (Object[]) value;
                    queryWrapper.between(column, range[0], range[1]);
                } else if (value instanceof String) {
                    String[] range = ((String) value).split(",");
                    if (range.length == 2) {
                        queryWrapper.between(column, range[0], range[1]);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("不支持的运算符: " + operator);
        }
    }
    
    private static <T> String getColumnName(String field, Class<T> entityClass) {
        try {
            // 这里可以使用反射获取字段上的@TableField注解值
            Field classField = entityClass.getDeclaredField(field);
            TableField tableField = classField.getAnnotation(TableField.class);
            if (tableField != null && !tableField.value().isEmpty()) {
                return tableField.value();
            }

            // 如果没有注解，默认使用字段名转下划线格式
            return StringUtils.camelToUnderline(field);
        } catch (Exception e) {
            return field;
        }
    }
}