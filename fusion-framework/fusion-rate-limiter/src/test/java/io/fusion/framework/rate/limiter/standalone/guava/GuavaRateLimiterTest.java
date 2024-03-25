package io.fusion.framework.rate.limiter.standalone.guava;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author enhao
 */
public class GuavaRateLimiterTest {

    @Test
    public void testSmoothBurstyRateLimiter() {
        RateLimiter rateLimiter = RateLimiter.create(5);
        System.out.printf("Get 10 tokens spend %f s%n", rateLimiter.acquire(10));
        System.out.printf("Get 1 token spend %f s%n", rateLimiter.acquire(1));
    }

    @Test
    public void testSmoothWarmingUpRateLimiter() {
        RateLimiter rateLimiter = RateLimiter.create(5, 3, TimeUnit.SECONDS);
        for (int i = 0; i < 20; i++) {
            double sleepingTime = rateLimiter.acquire(1);
            System.out.printf("get 1 tokens: %sds%n", sleepingTime);
        }
    }
}
