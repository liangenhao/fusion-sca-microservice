package io.fusion.common.statemachine;

/**
 * 状态机接口
 */
public interface StateMachine<S extends State, E extends Event> {
    /**
     * 获取当前状态
     */
    S getCurrentState();

    /**
     * 设置当前状态
     */
    void setCurrentState(S state);

    /**
     * 触发事件
     * @return 转换后的状态
     * @throws IllegalStateException 如果当前状态不能触发该事件
     */
    S fireEvent(E event) throws IllegalStateException;

    /**
     * 检查当前状态是否可以触发指定事件
     */
    boolean canFire(E event);

    /**
     * 获取指定事件的目标状态
     * @return 目标状态，如果当前状态不能触发该事件则返回null
     */
    S getTargetState(E event);

    /**
     * 重置状态机到初始状态
     */
    void reset();
}