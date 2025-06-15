package io.fusionsphere.ratelimit.distributed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author enhao
 */
@Slf4j
public abstract class AbstractRedisRateLimiter implements RedisRateLimiter {

    protected final RedisTemplate<String, String> redisTemplate;

    protected final RedisScript<List<Long>> redisScript;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public AbstractRedisRateLimiter(RedisTemplate<String, String> redisTemplate, String scriptClassPath) {
        this.redisTemplate = redisTemplate;
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(scriptClassPath)));
        redisScript.setResultType(List.class);
        this.redisScript = redisScript;
    }

    @Override
    public RateLimiterResponse isAllowed(String id, Integer replenishRate, Integer burstCapacity, Integer requestedTokens) {
        try {
            List<String> keys = getKeys(id);
            // 第三个参数为时间戳，为空默认通过redis TIME命令获取（单位秒）
            List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "", "", requestedTokens + "");
            List<Long> execResult = this.redisTemplate.execute(redisScript, keys, scriptArgs);
            if (null == execResult) {
                return new RateLimiterResponse(true, -1L, keys);
            }
            boolean allowed = execResult.get(0) == 1L;
            return new RateLimiterResponse(allowed, execResult.get(1).intValue(), keys);
        } catch (Exception e) {
            log.error("[RedisRateLimiter] Error determining if user allowed from redis", e);
        }
        return new RateLimiterResponse(true, -1L, Collections.emptyList());
    }
}
