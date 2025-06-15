package io.fusionsphere.spring.boot.cipher.core;

import io.fusionsphere.spring.boot.cipher.properties.CipherProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * spring-boot 2.4 版本后，若不引入 spring-cloud-starter-bootstrap，默认使用 {@code spring.config.import} 方式引入其他外部化配置，
 * 例如 Nacos 等配置中心。
 * <p>
 * {@code spring.config.import} 是通过 {@link ConfigDataEnvironmentPostProcessor} 来引导加载的，时机早于
 * {@link ApplicationContextInitializer}。
 * <p>
 *
 * @author enhao
 */
public class ConfigDecryptionEnvironmentPostProcessor extends AbstractConfigDecryption
        implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (bootstrapEnabled(environment) || useLegacyProcessing(environment)) {
            return;
        }
        Binder binder = Binder.get(environment);
        CipherProperties cipherProperties = binder.bind(CipherProperties.PREFIX, CipherProperties.class)
                .orElseGet(CipherProperties::new);
        if (!cipherProperties.isEnabled()) {
            return;
        }
        decryptEnvironment(environment, cipherProperties);
    }

    @Override
    public int getOrder() {
        return ConfigDataEnvironmentPostProcessor.ORDER + 1;
        // return Ordered.LOWEST_PRECEDENCE;
    }
}
