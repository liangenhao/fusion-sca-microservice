package io.fusion.distributed.transaction.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author enhao
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("io.fusion.distributed.transaction.order.mapper")
public class MybatisConfig {
}
