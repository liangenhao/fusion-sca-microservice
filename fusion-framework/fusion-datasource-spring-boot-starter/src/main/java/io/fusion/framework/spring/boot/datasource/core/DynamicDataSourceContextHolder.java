package io.fusion.framework.spring.boot.datasource.core;

import org.springframework.core.NamedThreadLocal;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 基于 ThreadLocal 的动态数据源管理器
 *
 * @author enhao
 */
public class DynamicDataSourceContextHolder {

    /**
     * 通过栈的方式（后进先出），支持数据源嵌套切换
     */
    private static final ThreadLocal<Deque<String>> LOOKUP_KEY_HOLDER = new NamedThreadLocal<Deque<String>>("dynamic-datasource") {
        @Override
        protected Deque<String> initialValue() {
            return new ArrayDeque<>();
        }
    };

    private DynamicDataSourceContextHolder() {
    }

    /**
     * 设置当前线程数据源，即将数据源写入栈顶
     *
     * @param dataSource 数据源名称
     * @return 切换后的数据源名称
     */
    public static String push(String dataSource) {
        String dataSourceStr = dataSource.isEmpty() ? "" : dataSource;
        LOOKUP_KEY_HOLDER.get().addFirst(dataSourceStr);
        return dataSourceStr;
    }

    /**
     * 移除当前数据源，即从栈顶移除数据源，达到后进先出的效果
     */
    public static void poll() {
        Deque<String> deque = LOOKUP_KEY_HOLDER.get();
        deque.pollFirst();
        if (deque.isEmpty()) {
            clear();
        }
    }

    /**
     * 获取当前数据源名称
     *
     * @return 数据源名称
     */
    public static String peek() {
        return LOOKUP_KEY_HOLDER.get().peek();
    }

    /**
     * 清空当前线程
     */
    public static void clear() {
        LOOKUP_KEY_HOLDER.remove();
    }

}
