package io.fusion.framework.rate.limiter.distributed;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * @author enhao
 */
public class SlidingWindowRedisRateLimiter extends AbstractRedisRateLimiter {

    public SlidingWindowRedisRateLimiter(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate, "META-INF/scripts/sliding_window_rate_limiter.lua");
    }

    @Override
    public List<String> getKeys(String id) {
        String prefix = "sliding_window_rate_limiter.{" + id;
        String tokenKey = prefix + "}.tokens";
        // String timestampKey = UUID.randomUUID().toString();
        String timestampKey = "0";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
