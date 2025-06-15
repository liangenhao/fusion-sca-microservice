package io.fusion.java.ioc.beans;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author enhao
 */
public class LockDemo {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Object mutex = new Object();

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                lock.lock();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            });

            // executorService.submit(() -> {
            //     synchronized (mutex) {
            //         try {
            //             Thread.sleep(10000);
            //         } catch (InterruptedException e) {
            //             e.printStackTrace();
            //         }
            //     }
            // });
        }

        executorService.shutdown();
        while (true) {
            try {
                if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

    }

    public void test() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        // ...
        executorService.shutdown();
        while (true) {
            try {
                if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                // Thread.currentThread().interrupt();
            }
        }
    }
}
