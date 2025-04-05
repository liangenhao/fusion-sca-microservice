package io.fusion.common.statemachine;

import io.fusion.api.enums.BaseEnum;

/**
 * 订单状态枚举
 */
public enum OrderState implements State, BaseEnum<Integer> {
    CREATED(0, "已创建"),      // 已创建
    PAID(1, "已支付"),         // 已支付
    SHIPPED(2, "已发货"),      // 已发货
    DELIVERED(3, "已送达"),    // 已送达
    COMPLETED(4, "已完成"),    // 已完成
    CANCELLED(5, "已取消")     // 已取消
    ;
    private final Integer code;
    private final String desc;

    OrderState(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}