package io.fusion.common.utils;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Consumer;

/**
 * Spring 事务工具类
 *
 * @author enhao
 */
public class TransactionUtils {

    private TransactionUtils() {
    }

    /**
     * 在事务提交前执行操作
     *
     * @param action 需要执行的操作
     */
    public static void runBeforeCommit(Consumer<Boolean> action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 如果当前不在事务中，直接执行操作，假定事务非只读
            action.accept(false);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCommit(boolean readOnly) {
                action.accept(readOnly);
            }
        });
    }

    /**
     * 在事务完成前执行操作（无论事务是提交还是回滚）
     *
     * @param action 需要执行的操作
     */
    public static void runBeforeCompletion(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 如果当前不在事务中，直接执行操作
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCompletion() {
                action.run();
            }
        });
    }

    /**
     * 在事务提交后执行操作
     *
     * @param action 需要执行的操作
     */
    public static void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 如果当前不在事务中，直接执行操作
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }


    /**
     * 在事务完成后执行操作（无论事务是提交还是回滚）
     *
     * @param action 需要执行的操作
     */
    public static void runAfterCompletion(Consumer<Integer> action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 如果当前不在事务中，直接执行操作，假定事务正常提交
            action.accept(TransactionSynchronization.STATUS_COMMITTED);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                action.accept(status);
            }
        });
    }

}
