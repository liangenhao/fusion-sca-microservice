package io.fusion.common.aspect;

import io.fusion.common.annotation.RepeatSubmit;
import io.fusion.common.utils.HttpRequestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RepeatSubmitAspect {

    private final RedissonClient redissonClient;

    public RepeatSubmitAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(repeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, RepeatSubmit repeatSubmit) throws Throwable {
        long interval = repeatSubmit.timeUnit().toMillis(repeatSubmit.interval());

        HttpServletRequest request = HttpRequestUtils.getRequest();
        String lockKey = generateKey(request, joinPoint, repeatSubmit);

        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(0, interval, TimeUnit.MILLISECONDS);
            if (!isLocked) {
                // todo 国际化支持
                throw new RuntimeException(repeatSubmit.message());
            }
            return joinPoint.proceed();
        } finally {
            // if (isLocked && lock.isHeldByCurrentThread()) {
            //     lock.unlock();
            // }
        }

    }

    private String generateKey(HttpServletRequest request, ProceedingJoinPoint joinPoint, RepeatSubmit repeatSubmit) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] array = Arrays.stream(joinPoint.getArgs()).filter(arg -> !this.isFilterObject(arg)).toArray();
        String args = Arrays.toString(array);
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(className).add(methodName).add(args);
        String md5 = DigestUtils.md5DigestAsHex(joiner.toString().getBytes());

        if (StringUtils.hasText(repeatSubmit.keyPrefix())) {
            return repeatSubmit.keyPrefix() + ":" + md5;
        }

        return request.getRequestURI() + ":" + md5;
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

}