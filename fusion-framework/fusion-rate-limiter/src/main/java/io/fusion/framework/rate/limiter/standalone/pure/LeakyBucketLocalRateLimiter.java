package io.fusion.framework.rate.limiter.standalone.pure;

import io.fusion.framework.rate.limiter.standalone.RateLimit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 单机版漏桶限流实现
 *
 * @author enhao
 */
public class LeakyBucketLocalRateLimiter implements RateLimit {

    /**
     * 桶的容量
     */
    private final long capacity;

    /**
     * 流出速率，单位为 tokens/s
     */
    private final long rate;

    /**
     * 当前桶内令牌数量
     */
    private final AtomicLong tokens;

    /**
     * 上次漏水的时间戳
     */
    private long lastLeakTime;

    public LeakyBucketLocalRateLimiter(long capacity, long rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.tokens = new AtomicLong(0);
        this.lastLeakTime = System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        return tryAcquire(1);
    }

    @Override
    public synchronized boolean tryAcquire(int permits) {
        // 漏水
        leak();
        if (tokens.get() + permits < capacity) {
            tokens.addAndGet(permits);
            return true;
        }
        return false;
    }

    private void leak() {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastLeakTime;
        // 时间间隔内漏出的水量
        long leaked = (timeElapsed / 1000) * rate;
        if (leaked > 0) {
            tokens.set(Math.max(0, tokens.get() - leaked)); // 更新桶中的水量
            lastLeakTime = currentTime; // 更新上一次漏水的时间
        }
    }
}
