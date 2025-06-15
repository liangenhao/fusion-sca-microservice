package io.fusionsphere.spring.boot.cipher.autoconfigure;

import io.fusionsphere.spring.boot.cipher.properties.CipherProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author enhao
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CipherProperties.class)
public class CipherAutoConfiguration {
}
