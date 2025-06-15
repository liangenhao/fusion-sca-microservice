package io.fusionsphere.spring.boot.datasource.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.Map;

/**
 * 对 {@link DataSourceProperties} 的扩展
 *
 * @author enhao
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExtensionDataSourceProperties extends DataSourceProperties {

    /**
     * 单数据源级别连接池配置
     * 具体连接池根据 {@link  DataSourceProperties#getType()} 值自动探测
     * 若未指定值，根据 HikariCP > Tomcat JDBC Pool > DBCP2 > Oracle UCP > c3p0 优先级选择连接池
     */
    private Map<String, Object> configuration;
}
