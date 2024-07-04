package io.fusion.framework.spring.boot.datasource.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

import static io.fusion.framework.spring.boot.datasource.properties.DynamicDataSourceProperties.PREFIX;

/**
 * @author enhao
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class DynamicDataSourceProperties {

    public static final String PREFIX = "fusion.datasource.dynamic";

    /**
     * 是否开启动态数据源配置
     */
    private boolean enabled = true;

    /**
     * 默认数据源
     */
    private String primary = "master";

    /**
     * 多数据源配置
     * key: 数据源名称
     * value: 数据源配置
     */
    @NestedConfigurationProperty
    private Map<String, ExtensionDataSourceProperties> datasource;

    /**
     * 连接池全局配置
     * <p>
     * 单数据源级别配置覆盖全局配置
     * <p>
     * 注意：若多个数据源分别用了不同的连接池，那么全局配置的属性需包含不同的连接池的属性
     *
     * @see ExtensionDataSourceProperties#getConfiguration()
     */
    private Map<String, Object> configuration;
}
