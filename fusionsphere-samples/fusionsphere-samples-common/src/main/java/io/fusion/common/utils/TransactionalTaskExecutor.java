package io.fusion.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Consumer;

/**
 * 事务执行器
 *
 * @author enhao
 */
public class TransactionalTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(TransactionalTaskExecutor.class);

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

    public static void runCommit(Consumer<Boolean> beforeCommit, Runnable beforeCompletion,
                                 Runnable afterCommit, Consumer<Integer> afterCompletion) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    if (beforeCommit != null) {
                        beforeCommit.accept(readOnly);
                    }
                }

                @Override
                public void beforeCompletion() {
                    if (beforeCompletion != null) {
                        beforeCompletion.run();
                    }
                }

                @Override
                public void afterCommit() {
                    if (afterCommit != null) {
                        afterCommit.run();
                    }
                }

                @Override
                public void afterCompletion(int status) {
                    if (afterCompletion != null) {
                        afterCompletion.accept(status);
                    }
                }
            });
            return;
        }

        // 若不处于事务中，模拟事务同步调用流程
        try {
            triggerBeforeCommit(beforeCommit, false);
            triggerBeforeCompletion(beforeCompletion);
        } catch (Throwable t) {
            triggerBeforeCompletion(beforeCompletion);

            triggerAfterCompletion(afterCompletion, TransactionSynchronization.STATUS_UNKNOWN);
            throw t;
        }

        try {
            triggerAfterCommit(afterCommit);
        } finally {
            triggerAfterCompletion(afterCompletion, TransactionSynchronization.STATUS_COMMITTED);
        }

    }

    private static void triggerBeforeCommit(Consumer<Boolean> beforeCommit, boolean isReadOnly) {
        if (beforeCommit != null) {
            beforeCommit.accept(isReadOnly);
        }
    }

    private static void triggerBeforeCompletion(Runnable beforeCompletion) {
        if (beforeCompletion != null) {
            try {
                beforeCompletion.run();
            } catch (Throwable t) {
                log.info("beforeCompletion threw exception", t);
            }
        }
    }

    private static void triggerAfterCommit(Runnable afterCommit) {
        if (afterCommit != null) {
            afterCommit.run();
        }
    }

    private static void triggerAfterCompletion(Consumer<Integer> afterCompletion, int status) {
        if (afterCompletion != null) {
            try {
                afterCompletion.accept(status);
            } catch (Throwable t) {
                log.info("afterCompletion threw exception", t);
            }
        }
    }

}
