package io.fusion.common.statemachine;

import java.util.Collection;

/**
 * 状态机配置接口
 */
public interface StateMachineConfig<S extends State, E extends Event> {
    /**
     * 获取所有可能的状态
     */
    Collection<S> getStates();

    /**
     * 获取所有可能的事件
     */
    Collection<E> getEvents();

    /**
     * 获取所有状态转换规则
     */
    Collection<StateTransition<S, E>> getTransitions();

    /**
     * 获取初始状态
     */
    S getInitialState();
}