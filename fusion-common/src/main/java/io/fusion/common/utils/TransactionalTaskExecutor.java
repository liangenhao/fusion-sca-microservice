package io.fusion.common.utils;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 事务执行器
 *
 * @author enhao
 */
public class TransactionalTaskExecutor {

    /**
     * 在事务提交后执行指定的操作，如果没有事务，则直接执行。
     *
     * @param task 要执行的操作（Consumer）
     */
    public static void executeAfterCommit(Runnable task) {
        // 检查是否在事务中
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // 如果在事务中，注册事务同步回调
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    task.run(); // 事务提交后执行任务
                }

                @Override
                public void afterCompletion(int status) {
                    if (status != TransactionSynchronization.STATUS_COMMITTED) {
                        // 事务未提交成功时的逻辑
                        System.err.println("Transaction rolled back. Task not executed.");
                    }
                }
            });
        } else {
            // 如果没有事务，直接执行任务
            task.run();
        }
    }
}
