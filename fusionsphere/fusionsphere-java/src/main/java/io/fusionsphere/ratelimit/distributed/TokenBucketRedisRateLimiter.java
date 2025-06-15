package io.fusionsphere.ratelimit.distributed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Redis实现令牌桶算法限流
 *
 * @author enhao
 */
@Slf4j
public class TokenBucketRedisRateLimiter extends AbstractRedisRateLimiter {

    public TokenBucketRedisRateLimiter(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate, "META-INF/scripts/token_bucket_rate_limiter.lua");
    }

    @Override
    public List<String> getKeys(String id) {
        String prefix = "token_bucket_rate_limiter.{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
