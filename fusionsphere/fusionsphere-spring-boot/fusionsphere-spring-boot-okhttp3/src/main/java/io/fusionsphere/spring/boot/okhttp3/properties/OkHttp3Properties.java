package io.fusionsphere.spring.boot.okhttp3.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * OkHttp3Properties
 *
 * @author enhao
 */
@Data
@ConfigurationProperties(prefix = OkHttp3Properties.PREFIX)
public class OkHttp3Properties {

    public static final String PREFIX = "fusionsphere.okhttp3";

    private boolean enabled = true;

    /**
     * 连接超时时间，单位毫秒，默认 10 秒
     */
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration connectTimeout = Duration.ofMillis(10_000);

    /**
     * 读取超时时间，单位毫秒，默认 10 秒
     */
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration readTimeout = Duration.ofMillis(10_000);

    /**
     * 写入超时时间，单位毫秒，默认 10 秒
     */
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration writeTimeout = Duration.ofMillis(10_000);

    /**
     * websocket ping间隔时间，单位毫秒，默认 0，表示禁用 ping
     */
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration pingInterval = Duration.ofMillis(0);

    /**
     * 是否允许重定向，默认 true
     */
    private boolean followRedirects = true;

    /**
     * 将此客户端配置为遵循从 HTTPS 到 HTTP 以及从 HTTP 到 HTTPS 的重定向。
     * 如果未设置，将遵循协议重定向。这与内置 HttpURLConnection 的默认值不同。
     */
    private boolean followSslRedirects = true;

    /**
     * 将此客户端配置为在遇到连接问题时重试或不重试
     */
    private boolean retryOnConnectionFailure = true;

    /**
     * 连接池配置
     */
    private PoolProperties pool = new PoolProperties();

    /**
     * 全局缓存配置
     */
    private CacheProperties cache = new CacheProperties();

    /**
     * 连接池配置
     *
     * @author enhao
     */
    @Data
    public static class PoolProperties {

        /**
         * 每个地址的最大空闲连接数，默认 5
         */
        private Integer maxIdleConnections = 5;

        /**
         * 连接存活时间，单位分钟，默认 5 分钟
         */
        @DurationUnit(ChronoUnit.MINUTES)
        private Duration keepAliveDuration = Duration.ofMinutes(5);
    }

    /**
     * 全局缓存配置
     *
     * @author enhao
     */
    @Data
    public static class CacheProperties {

        private boolean enabled = false;

        /**
         * 应存储缓存的目录的路径
         */
        private File directory;

        /**
         * 此缓存应用于存储的最大字节数，单位 MB，默认 10 MB
         */
        @DataSizeUnit(DataUnit.MEGABYTES)
        private DataSize maxSize = DataSize.ofMegabytes(10);
    }

}
