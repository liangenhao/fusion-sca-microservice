package io.fusion.common.statemachine;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 订单状态机配置
 */
public class OrderStateMachineConfig implements StateMachineConfig<OrderState, OrderEvent> {

    public static final OrderStateMachineConfig INSTANCE = new OrderStateMachineConfig();
    
    private final Set<StateTransition<OrderState, OrderEvent>> transitions = new HashSet<>();
    
    public OrderStateMachineConfig() {
        // 定义状态转换规则
        transitions.add(StateTransition.of(OrderState.CREATED, OrderState.PAID, OrderEvent.PAY));
        transitions.add(StateTransition.of(OrderState.CREATED, OrderState.CANCELLED, OrderEvent.CANCEL));
        transitions.add(StateTransition.of(OrderState.PAID, OrderState.SHIPPED, OrderEvent.SHIP));
        transitions.add(StateTransition.of(OrderState.PAID, OrderState.CANCELLED, OrderEvent.CANCEL));
        transitions.add(StateTransition.of(OrderState.SHIPPED, OrderState.DELIVERED, OrderEvent.DELIVER));
        transitions.add(StateTransition.of(OrderState.DELIVERED, OrderState.COMPLETED, OrderEvent.COMPLETE));
    }
    
    @Override
    public Collection<OrderState> getStates() {
        return Arrays.asList(OrderState.values());
    }
    
    @Override
    public Collection<OrderEvent> getEvents() {
        return Arrays.asList(OrderEvent.values());
    }
    
    @Override
    public Collection<StateTransition<OrderState, OrderEvent>> getTransitions() {
        return transitions;
    }
    
    @Override
    public OrderState getInitialState() {
        return OrderState.CREATED;
    }
}