package io.fusion.common.statemachine;

/**
 * 订单事件枚举
 */
public enum OrderEvent implements Event {
    PAY,          // 支付
    SHIP,         // 发货
    DELIVER,      // 送达
    COMPLETE,     // 完成
    CANCEL        // 取消
}