package io.fusion.common.utils;

import io.fusion.api.enums.BaseEnum;

/**
 * @author enhao
 */
public enum GenderEnum /*implements BaseEnum<Integer>*/ {
    MALE(1, "男"),
    FEMALE(2, "女");

    private final Integer code;

    private final String desc;

    GenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // @Override
    public Integer getCode() {
        return code;
    }

    // @Override
    public String getDesc() {
        return desc;
    }
}
