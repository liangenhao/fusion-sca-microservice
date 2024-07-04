package io.fusion.framework.spring.boot.datasource.autoconfigure;

import io.fusion.framework.spring.boot.datasource.core.DefaultDynamicDataSourceAspect;
import io.fusion.framework.spring.boot.datasource.core.DynamicDataSourceAspect;
import io.fusion.framework.spring.boot.datasource.core.DynamicRoutingDataSource;
import io.fusion.framework.spring.boot.datasource.properties.DynamicDataSourceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 动态数据源自动装配
 *
 * @author enhao
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DynamicDataSourceAspect.class)
    public DefaultDynamicDataSourceAspect defaultDynamicDataSourceAspect() {
        return new DefaultDynamicDataSourceAspect();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DataSource dynamicDataSource(DynamicDataSourceProperties properties) {
        return new DynamicRoutingDataSource(properties);
    }
}
