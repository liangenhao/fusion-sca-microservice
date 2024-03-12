package io.fusion.framework.core.tools.idgen.snowflake;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link Snowflake} 对象生成器
 * <p>
 * 在分布式场景中，需保持获取的{@link Snowflake}对象唯一。
 * 可以是全局唯一，也可以是按业务唯一。
 *
 * @author enhao
 */
public class SnowflakeGenerator {

    /**
     * 数据大小
     */
    private static final Integer DATA_SIZE = 32;

    /**
     * 32进制的基数
     */
    private static final String[] RADIX_STR = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v"
    };

    /**
     * 32进制基数和10进制的映射关系
     */
    private static final Map<String, Integer> RADIX_MAP = new LinkedHashMap<>();

    static {
        for (int i = 0; i < DATA_SIZE; i++) {
            RADIX_MAP.put(RADIX_STR[i], i);
        }
    }

    private SnowflakeGenerator() {
    }

    public static Snowflake getSnowflake(long sequence) {
        return getSnowflake(sequence, false);
    }

    public static Snowflake getSnowflake(long sequence, boolean isUseSystemClock) {
        return getSnowflake(sequence, isUseSystemClock, 0);
    }

    /**
     * 基于传入的序列，动态生成 workerId 和 dataCenterId， 从而创建 {@link Snowflake} 对象
     * <p>
     * 注意：按场景保持唯一
     *
     * @param sequence            序列，同场景保持唯一
     * @param isUseSystemClock    是否使用{@link SystemClock} 获取当前时间戳
     * @param randomSequenceLimit 限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题，0表示无随机，上限不包括值本身。
     * @return {@link Snowflake}
     */
    public static Snowflake getSnowflake(long sequence, boolean isUseSystemClock, long randomSequenceLimit) {
        // 序列和1024取余，保证参与计算的结果范围在 [0, 1023]
        // 因为在32进制中，1023是可以使用两个32进制数表示的最大值
        long mod = sequence % ((long) DATA_SIZE * DATA_SIZE);
        // 将余数转换成32进制字符串
        String base32 = Integer.toString(Math.toIntExact(mod), 32);
        // 不足两位的前面补0
        base32 = StringUtils.leftPad(base32, 2, "0");
        // 第一位用作机器ID
        Integer workerId = RADIX_MAP.get(base32.substring(0, 1));
        // 第二位用作数据中心ID
        Integer dataCenterId = RADIX_MAP.get(base32.substring(1, 2));

        return new Snowflake(null, workerId, dataCenterId, isUseSystemClock, Snowflake.DEFAULT_TIME_OFFSET,
                randomSequenceLimit);
    }

    public static Snowflake getSnowflake(RedisTemplate redisTemplate, String sceneName) {
        return getSnowflake(redisTemplate, sceneName, false);
    }

    public static Snowflake getSnowflake(RedisTemplate redisTemplate, String sceneName, boolean isUseSystemClock) {
        return getSnowflake(redisTemplate, sceneName, isUseSystemClock, 0);
    }

    /**
     * 通过 {@link RedisTemplate} 动态生成 workerId 和 dataCenterId， 从而创建 {@link Snowflake} 对象
     *
     * @param redisTemplate       {@link RedisTemplate}
     * @param sceneName           场景名称，用作 redis 缓存key
     * @param isUseSystemClock    是否使用{@link SystemClock} 获取当前时间戳
     * @param randomSequenceLimit 限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题，0表示无随机，上限不包括值本身。
     * @return {@link Snowflake}
     */
    public static Snowflake getSnowflake(RedisTemplate redisTemplate, String sceneName, boolean isUseSystemClock,
                                         long randomSequenceLimit) {
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (null == connectionFactory) {
            throw new IllegalStateException("RedisConnectionFactory is required");
        }
        String cacheKey = "snowflake:scene:" + sceneName;
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(cacheKey, connectionFactory);
        long sequence = redisAtomicLong.getAndIncrement();

        return getSnowflake(sequence, isUseSystemClock, randomSequenceLimit);
    }

}
