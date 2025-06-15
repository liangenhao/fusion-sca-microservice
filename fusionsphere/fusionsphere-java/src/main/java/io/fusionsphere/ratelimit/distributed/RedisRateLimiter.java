package io.fusionsphere.ratelimit.distributed;

import java.util.List;

/**
 * @author enhao
 */
public interface RedisRateLimiter {

    /**
     * 是否通过
     *
     * @param id              ID，缓存key的一部分，用于区分不同的限流维度
     * @param replenishRate   速率
     * @param burstCapacity   容量
     * @param requestedTokens 请求的token数
     * @return {@link RateLimiterResponse}
     */
    RateLimiterResponse isAllowed(String id, Integer replenishRate, Integer burstCapacity, Integer requestedTokens);

    /**
     * 获取缓存key
     *
     * @param id ID，缓存key的一部分，用于区分不同的限流维度
     * @return {@link List}
     */
    List<String> getKeys(String id);

}
