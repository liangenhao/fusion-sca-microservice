package io.fusionsphere.spring.boot.cipher.core;

import io.fusionsphere.spring.boot.cipher.properties.CipherProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.NonNull;

/**
 * {@link ApplicationContextInitializer} 实现，
 * 用于在外部化配置中，针对以 {@link #ENCRYPTED_PROPERTY_PREFIX} 开头的配置项的值进行解密。
 * <p>
 * 本类的调用顺序必须在所有的配置数据源 {@link PropertySource} 加载后进行，否则会造成遗漏。
 * <p>
 * 本类的执行顺序 {@link #getOrder()} 需在 {@code PropertySourceBootstrapConfiguration} 之后。
 * （{@code NacosPropertySource} 在 {@code PropertySourceBootstrapConfiguration} 中加载）
 * <p>
 * 同时支持配置刷新时动态更新
 * <p>
 * 当 bootstrap.yml 配置中加密配置项需要解密，加密配置需要在 bootstrap.yml 中配置，或者配置在环境变量/JVM 参数中
 *
 * @author enhao
 */
public class ConfigDecryptionApplicationContextInitializer extends AbstractConfigDecryption
        implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        if (!bootstrapEnabled(environment) && !useLegacyProcessing(environment)) {
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
        return Ordered.HIGHEST_PRECEDENCE + 16;
    }

}
