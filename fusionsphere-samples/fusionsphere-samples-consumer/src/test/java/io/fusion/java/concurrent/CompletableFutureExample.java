package io.fusion.java.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建CompletableFuture
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 模拟一个耗时任务
            return "Hello World";
        });

        // 链式操作
        future.thenApply(result -> result + " from CompletableFuture")
                .thenAccept(System.out::println)
                .thenRun(() -> System.out.println("Task completed."));

        // 异常处理
        future.exceptionally(ex -> {
            System.out.println("Exception occurred: " + ex.getMessage());
            return "Default Value";
        });

        // 组合多个 CompletableFuture
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Task 1");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Task 2");

        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(future1, future2);
        allOfFuture.thenRun(() -> System.out.println("All tasks completed"));

        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2);
        anyOfFuture.thenAccept(result -> System.out.println("First completed task result: " + result));

        // 合并两个结果
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> 2);
        CompletableFuture<Integer> future4 = CompletableFuture.supplyAsync(() -> 3);

        future3.thenCombine(future4, Integer::sum)
                .thenAccept(result -> System.out.println("Sum: " + result));
    }
}


