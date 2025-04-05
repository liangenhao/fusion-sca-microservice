package io.fusion.common.statemachine;

import lombok.Data;

/**
 * 状态转换定义
 */
@Data
public class StateTransition<S extends State, E extends Event> {
    private final S sourceState;
    private final S targetState;
    private final E event;

    public StateTransition(S sourceState, S targetState, E event) {
        this.sourceState = sourceState;
        this.targetState = targetState;
        this.event = event;
    }

    public static <S extends State, E extends Event> StateTransition<S, E> of(S sourceState, S targetState, E event) {
        return new StateTransition<>(sourceState, targetState, event);
    }
}