package io.fusion.common.statemachine;

/**
 * 状态机上下文工厂
 */
public class StateMachineFactory {

    /**
     * 创建新的状态机上下文
     *
     * @param config 状态机配置
     * @return 状态机对象
     */
    public static <S extends State, E extends Event> DefaultStateMachine<S, E> create(StateMachineConfig<S, E> config) {
        return new DefaultStateMachine<>(config);
    }

    /**
     * 创建指定初始状态的状态机上下文
     *
     * @param config       状态机配置
     * @param initialState 初始状态
     * @return 状态机对象
     */
    public static <S extends State, E extends Event> DefaultStateMachine<S, E> create(StateMachineConfig<S, E> config, S initialState) {
        return new DefaultStateMachine<>(config, initialState);
    }

}