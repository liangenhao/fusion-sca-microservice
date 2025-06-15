package io.fusionsphere.ratelimit.distributed;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * redis实现漏桶算法限流
 *
 * @author enhao
 */
public class LeakyBucketRedisRateLimiter extends AbstractRedisRateLimiter {

    public LeakyBucketRedisRateLimiter(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate, "META-INF/scripts/leaky_bucket_rate_limiter.lua");
    }

    @Override
    public List<String> getKeys(String id) {
        String prefix = "leaky_bucket_rate_limiter.{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
