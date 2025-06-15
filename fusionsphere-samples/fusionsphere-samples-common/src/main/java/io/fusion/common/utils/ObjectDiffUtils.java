package io.fusion.common.utils;

import io.fusion.api.annotation.CompareField;
import io.fusion.api.annotation.KeyField;
import io.fusion.api.enums.BaseEnum;
import io.fusion.api.model.FieldChange;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author enhao
 */
public class ObjectDiffUtils {

    /**
     * 比较两个对象字段差异
     *
     * @param oldObj 原始对象
     * @param newObj 新对象
     * @return 变更记录列表
     */
    public static List<FieldChange> compare(Object oldObj, Object newObj) {
        return compare(oldObj, newObj, "", 0, 0);
    }

    /**
     * 比较两个对象字段差异
     *
     * @param oldObj       原始对象
     * @param newObj       新对象
     * @param path         字段路径
     * @param currentDepth 当前递归深度
     * @param allowedDepth 允许的最大递归深度
     * @return 变更记录列表
     */
    @SneakyThrows
    private static List<FieldChange> compare(Object oldObj, Object newObj, String path, int currentDepth, int allowedDepth) {
        if (oldObj == null || newObj == null) {
            return new ArrayList<>();
        }
        if (!oldObj.getClass().equals(newObj.getClass())) {
            throw new IllegalArgumentException("只有相同类型对象才可以比较");
        }

        Class<?> cls = oldObj.getClass();
        Field[] fields = cls.getDeclaredFields();

        List<FieldChange> changes = new ArrayList<>(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);

            CompareField compareField = field.getAnnotation(CompareField.class);
            if (compareField == null) {
                continue;
            }
            Object oldValue = field.get(oldObj);
            Object newValue = field.get(newObj);

            String currentPath = path.isEmpty() ? compareField.name() : path + "." + compareField.name();
            if (compareField.nested()) {
                // 嵌套对象递归比较
                int newAllowedDepth = (currentDepth == 0) ? compareField.maxDepth() : allowedDepth;
                if (currentDepth >= newAllowedDepth) {
                    continue;
                }
                List<FieldChange> nestedChanges;
                if (oldValue instanceof Collection && newValue instanceof Collection) {
                    nestedChanges = compareCollections(field, (Collection<?>) oldValue, (Collection<?>) newValue, compareField,
                            currentPath, currentDepth + 1, newAllowedDepth);
                } else {
                    nestedChanges = compare(oldValue, newValue, currentPath, currentDepth + 1, newAllowedDepth);
                }
                changes.addAll(nestedChanges);

                continue;
            }

            if (!Objects.equals(oldValue, newValue)) {
                // 记录字段差异
                FieldChange fieldChange = resolveFieldChange(field, currentPath, oldValue, newValue, compareField);
                changes.add(fieldChange);
            }
        }
        return changes;
    }

    /**
     * 格式化变更记录为可读字符串
     *
     * @param oldObj 原始对象
     * @param newObj 新对象
     * @return 变更记录字符串
     */
    public static String formatChanges(Object oldObj, Object newObj) {
        List<FieldChange> fieldChanges = compare(oldObj, newObj);

        if (CollectionUtils.isEmpty(fieldChanges)) {
            return StringUtils.EMPTY;
        }

        return formatChanges(fieldChanges);
    }

    public static String formatChanges(List<FieldChange> fieldChanges) {
        StringBuilder msg = new StringBuilder();
        for (FieldChange fieldChange : fieldChanges) {
            CompareField compareField = fieldChange.getCompareField();
            if (compareField.ignore()) {
                continue;
            }
            msg.append(String.format("%s: %s -> %s；", fieldChange.getFieldAlias(), fieldChange.getOldDisplayValue(),
                    fieldChange.getNewDisplayValue()));
        }
        return msg.substring(0, msg.length() - 1);
    }

    /**
     * 判断关键字段是否有变更
     *
     * @param oldObj 原始对象
     * @param newObj 新对象
     * @return true 有变更，false 无变更
     */
    @SneakyThrows
    public static boolean isKeyFieldChanged(Object oldObj, Object newObj) {
        if (oldObj == null || newObj == null) {
            return false;
        }
        if (oldObj.getClass().equals(newObj.getClass())) {
            return false;
        }

        Class<?> cls = oldObj.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            KeyField keyField = field.getAnnotation(KeyField.class);
            if (keyField == null) {
                continue;
            }
            Object oldValue = field.get(oldObj);
            Object newValue = field.get(newObj);
            if (!Objects.equals(oldValue, newValue)) {
                return true;
            }
        }

        return false;
    }

    // ==================== private method ====================

    private static List<FieldChange> compareCollections(Field field, Collection<?> oldCol, Collection<?> newCol,
                                                        CompareField compareField, String basePath, int depth, int allowedDepth) {
        List<FieldChange> changes = new ArrayList<>(Math.max(oldCol.size(), newCol.size()));
        List<?> oldList = new ArrayList<>(oldCol);
        List<?> newList = new ArrayList<>(newCol);

        // 比较修改和删除的元素
        for (int i = 0; i < oldList.size(); i++) {
            String elementPath = basePath + "[" + i + "]";
            if (i < newList.size()) {
                // 递归比较相同位置的元素
                changes.addAll(compare(oldList.get(i), newList.get(i), elementPath, depth, allowedDepth));
            } else {
                // 记录被删除的元素
                FieldChange fieldChange = resolveFieldChange(field, elementPath, oldList.get(i), null, compareField);
                changes.add(fieldChange);
            }
        }

        // 记录新增的元素
        for (int i = oldList.size(); i < newList.size(); i++) {
            String elementPath = basePath + "[" + i + "]";
            FieldChange fieldChange = resolveFieldChange(field, elementPath, null, newList.get(i), compareField);
            changes.add(fieldChange);
        }

        return changes;
    }

    private static FieldChange resolveFieldChange(Field field, String fieldAlias, Object oldValue, Object newValue,
                                                  CompareField compareField) {
        String name = field.getDeclaringClass().getSimpleName() + "." + field.getName();
        String oldDisplayValue = formatValue(oldValue, compareField);
        String newDisplayValue = formatValue(newValue, compareField);

        return FieldChange.builder()
                .field(name)
                .fieldAlias(fieldAlias)
                .oldValue(oldValue)
                .oldDisplayValue(oldDisplayValue)
                .newValue(newValue)
                .newDisplayValue(newDisplayValue)
                .compareField(compareField)
                .build();
    }

    private static String formatValue(Object value, CompareField compareField) {
        if (value == null) {
            return "空";
        }

        if (value instanceof Enum) {
            return formatEnumObj((Enum<?>) value, compareField);
        } else if (value instanceof Temporal) {
            return formatTemporal((Temporal) value, compareField);
        } else if (value instanceof Date) {
            return formatDate((Date) value, compareField);
        }

        if (compareField.enumClass() != Void.class) {
            return formatEnumValue(value, compareField);
        }

        return value.toString();
    }

    private static String formatEnumObj(Enum<?> value, CompareField compareField) {
        if (StringUtils.isNotBlank(compareField.enumDisplayField())) {
            try {
                Field enumField = value.getClass().getDeclaredField(compareField.enumDisplayField());
                enumField.setAccessible(true);
                return enumField.get(value).toString();
            } catch (Exception e) {
                return value.toString();
            }
        }

        if (value instanceof BaseEnum) {
            return ((BaseEnum<?>) value).getDesc();
        }

        // 兜底规则
        return value.name();
    }

    private static String formatEnumValue(Object value, CompareField compareField) {
        Class<?> enumClass = compareField.enumClass();
        if (enumClass == Void.class) {
            return value.toString();
        }

        Enum<?>[] enums = (Enum<?>[]) enumClass.getEnumConstants();
        try {
            Field field = enumClass.getDeclaredField(compareField.enumCodeField());
            field.setAccessible(true);
            for (Enum<?> e : enums) {
                if (Objects.equals(field.get(e), value)) {
                    return formatEnumObj(e, compareField);
                }
            }
        } catch (Exception ex) {
            return value.toString();
        }

        return value.toString();
    }

    private static String formatTemporal(Temporal value, CompareField compareField) {
        if (compareField.dateFormat().isEmpty()) {
            return value.toString();
        }

        if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern(compareField.dateFormat()));
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(compareField.dateFormat()));
        } else if (value instanceof LocalTime) {
            return ((LocalTime) value).format(DateTimeFormatter.ofPattern(compareField.dateFormat()));
        }

        return value.toString();
    }

    private static String formatDate(Date value, CompareField compareField) {
        if (compareField.dateFormat().isEmpty()) {
            return value.toString();
        }

        return new SimpleDateFormat(compareField.dateFormat()).format(value);
    }

}
