package io.fusionsphere.ratelimit.standalone.pure;

import io.fusionsphere.ratelimit.standalone.RateLimit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 单机版令牌桶限流实现
 *
 * @author enhao
 */
public class TokenBucketLocalRateLimiter implements RateLimit {

    /**
     * 令牌桶容量
     */
    private final long capacity;

    /**
     * 令牌生成速率，单位：令牌/秒
     */
    private final long refillRate;

    /**
     * 当前令牌数量
     */
    private final AtomicLong tokens;

    /**
     * 上一次令牌生成时间戳
     */
    private long lastRefillTimestamp;

    public TokenBucketLocalRateLimiter(long capacity, long refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean tryAcquire() {
        return tryAcquire(1);
    }

    /**
     * 尝试获取令牌，返回 true 表示获取成功，返回 false 表示获取失败
     *
     * @return {@code boolean}
     */
    @Override
    public synchronized boolean tryAcquire(int permits) {
        // 先补充令牌
        refill();
        if (tokens.get() >= permits) {
            tokens.addAndGet(-permits);
            return true;
        }
        return false;
    }

    /**
     * 补充令牌
     */
    private synchronized void refill() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastRefillTimestamp;
        // 计算经过的时间内应该补充的令牌数量
        long tokensToAdd = (elapsedTime / 1000) * refillRate;
        if (tokensToAdd > 0) {
            // 更新令牌数量，但不超过容量
            tokens.set(Math.min(capacity, tokens.get() + tokensToAdd));
            // 更新最后补充令牌的时间戳
            lastRefillTimestamp = currentTime;
        }
    }
}
