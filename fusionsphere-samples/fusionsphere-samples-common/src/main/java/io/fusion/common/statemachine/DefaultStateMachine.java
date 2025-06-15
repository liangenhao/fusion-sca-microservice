package io.fusion.common.statemachine;

import com.google.common.collect.Maps;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 状态机
 */
public class DefaultStateMachine<S extends State, E extends Event> implements StateMachine<S, E> {

    private final StateMachineConfig<S, E> config;
    private final Map<StateEventKey, S> transitionMap;
    private S currentState;

    // 业务对象ID，用于关联具体业务实例
    private String businessId;

    public DefaultStateMachine(StateMachineConfig<S, E> config) {
        Assert.notNull(config, "配置不能为空");
        this.config = config;
        this.currentState = config.getInitialState();
        this.transitionMap = buildTransitionMap(config);
    }

    public DefaultStateMachine(StateMachineConfig<S, E> config, S initialState) {
        Assert.notNull(config, "配置不能为空");
        Assert.notNull(initialState, "初始状态不能为空");
        this.config = config;
        this.currentState = initialState;
        this.transitionMap = buildTransitionMap(config);
    }

    private Map<StateEventKey, S> buildTransitionMap(StateMachineConfig<S, E> config) {
        Collection<StateTransition<S, E>> stateTransitions = config.getTransitions();
        Map<StateEventKey, S> map = Maps.newHashMapWithExpectedSize(stateTransitions.size());
        for (StateTransition<S, E> transition : stateTransitions) {
            StateEventKey key = new StateEventKey(transition.getSourceState(), transition.getEvent());
            map.put(key, transition.getTargetState());
        }
        return map;
    }

    /**
     * 获取当前状态
     */
    public S getCurrentState() {
        return currentState;
    }

    /**
     * 设置当前状态
     */
    public void setCurrentState(S state) {
        Assert.notNull(state, "状态不能为空");
        this.currentState = state;
    }

    /**
     * 获取业务ID
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * 设置业务ID
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * 检查当前状态是否可以触发指定事件
     */
    public boolean canFire(E event) {
        Assert.notNull(event, "事件不能为空");
        return getTargetState(event) != null;
    }

    /**
     * 获取指定事件的目标状态
     */
    public S getTargetState(E event) {
        Assert.notNull(event, "事件不能为空");
        return transitionMap.get(new StateEventKey(currentState, event));
    }

    /**
     * 触发事件
     *
     * @return 转换后的状态
     * @throws IllegalStateException 如果当前状态不能触发该事件
     */
    public S fireEvent(E event) throws IllegalStateException {
        Assert.notNull(event, "事件不能为空");

        S targetState = getTargetState(event);
        if (targetState == null) {
            throw new IllegalStateException(
                    String.format("当前状态 [%s] 不能触发事件 [%s]", currentState.name(), event.name())
            );
        }

        this.currentState = targetState;
        return this.currentState;
    }

    /**
     * 重置状态机到初始状态
     */
    public void reset() {
        this.currentState = config.getInitialState();
    }

    /**
     * 创建当前状态机上下文的副本
     */
    public DefaultStateMachine<S, E> copy() {
        DefaultStateMachine<S, E> copy = new DefaultStateMachine<>(this.config, this.currentState);
        copy.setBusinessId(this.businessId);
        return copy;
    }

    /**
     * 状态-事件组合键，用于在Map中查找目标状态
     */
    private class StateEventKey {
        private final S state;
        private final E event;

        public StateEventKey(S state, E event) {
            this.state = state;
            this.event = event;
        }


        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            StateEventKey that = (StateEventKey) o;
            return Objects.equals(state, that.state) && Objects.equals(event, that.event);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, event);
        }
    }
}