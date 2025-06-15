package io.fusionsphere.ratelimit.standalone.pure;

import io.fusionsphere.ratelimit.standalone.RateLimit;

import java.util.LinkedList;

/**
 * 单机版滑动窗口算法限流实现
 *
 * @author enhao
 */
public class SlidingWindowLocalRateLimiter implements RateLimit {


    private final LinkedList<Long> requestQueue = new LinkedList<>();

    /**
     * 窗口的大小（以毫秒为单位）
     */
    private final int windowSizeInMs;

    /**
     * 在窗口期内允许的最大请求量
     */
    private final int maxRequests;

    /**
     * 每个请求之间的时间间隔
     */
    private final long requestIntervalInMs;

    public SlidingWindowLocalRateLimiter(int windowSizeInMs, int maxRequests) {
        this.windowSizeInMs = windowSizeInMs;
        this.maxRequests = maxRequests;
        this.requestIntervalInMs = windowSizeInMs / maxRequests;
    }

    @Override
    public synchronized boolean tryAcquire() {
        return tryAcquire(1);
    }

    @Override
    public synchronized boolean tryAcquire(int permits) {
        long currentTime = System.currentTimeMillis();
        // 计算窗口起始时间: 当前时间 - 窗口大小
        long windowStart = currentTime - windowSizeInMs;

        // 遍历请求队列 requestQueue，移除队列中所有早于 windowStart 的请求，以确保队列中只包含在当前窗口内的请求
        while (!requestQueue.isEmpty() && requestQueue.peek() <= windowStart) {
            requestQueue.poll();
        }

        // 检查请求队列的大小是否小于 maxRequests。如果是，则表示当前窗口内的请求数量未达到限制，可以允许新的请求。
        if (requestQueue.size() < maxRequests) {
            // 将当前时间 currentTime 添加到请求队列末尾，表示发起了一个新的请求。
            for (int i = 0; i < permits; i++) {
                requestQueue.offer(currentTime);
            }
            return true;
        }

        // 如果请求队列的大小已经达到或超过了 maxRequests，则表示当前窗口内的请求数量已达到限制，不再允许新的请求。
        return false;
    }
}
