package io.fusion.framework.spring.boot.cipher.core;

import io.fusion.framework.spring.boot.cipher.core.encryptor.EncryptorFactory;
import io.fusion.framework.spring.boot.cipher.core.encryptor.TextEncryptor;
import io.fusion.framework.spring.boot.cipher.properties.CipherProperties;
import org.springframework.core.env.*;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author enhao
 */
public abstract class AbstractConfigDecryption {

    public static final String BOOTSTRAP_ENABLED_PROPERTY = "spring.cloud.bootstrap.enabled";

    public static final String USE_LEGACY_PROCESSING_PROPERTY = "spring.config.use-legacy-processing";

    public static final String MARKER_CLASS = "org.springframework.cloud.bootstrap.marker.Marker";

    public static final boolean MARKER_CLASS_EXISTS = ClassUtils.isPresent(MARKER_CLASS, null);

    /**
     * 加密值的前缀
     */
    public static final String ENCRYPTED_PROPERTY_PREFIX = "[cipher]";

    /**
     * 解密后创建的 {@link PropertySource} 名称
     */
    public static final String DECRYPTED_PROPERTY_SOURCE_NAME = "decryptedConfig";

    /**
     * 集合属性正则表达式
     * {@code (\S+)?\[(\d+)\](\.\S+)?}
     * 例如：arr[0]、list[1].property
     */
    private static final Pattern COLLECTION_PROPERTY = Pattern.compile("(\\S+)?\\[(\\d+)\\](\\.\\S+)?");

    public static boolean bootstrapEnabled(Environment environment) {
        return environment.getProperty(BOOTSTRAP_ENABLED_PROPERTY, Boolean.class, false) || MARKER_CLASS_EXISTS;
    }

    public static boolean useLegacyProcessing(Environment environment) {
        return environment.getProperty(USE_LEGACY_PROCESSING_PROPERTY, Boolean.class, false);
    }

    /**
     * 对环境参数值解密
     *
     * @param environment      环境对象
     * @param cipherProperties 加密配置
     */
    protected void decryptEnvironment(ConfigurableEnvironment environment, CipherProperties cipherProperties) {
        MutablePropertySources propertySources = environment.getPropertySources();
        String decryptedPropertySourceName = DECRYPTED_PROPERTY_SOURCE_NAME;

        // 解密
        Map<String, Object> decryptedProperties = decrypt(propertySources, cipherProperties);

        if (!decryptedProperties.isEmpty()) {
            environment.getPropertySources().remove(decryptedPropertySourceName);
            propertySources.addFirst(new MapPropertySource(decryptedPropertySourceName, decryptedProperties));
        }
    }

    /**
     * 对 {@link MutablePropertySources} 中的所有属性值解密
     *
     * @param propertySources  {@link MutablePropertySources}
     * @param cipherProperties 加密配置
     * @return 解密后的属性值键值对
     */
    protected Map<String, Object> decrypt(MutablePropertySources propertySources, CipherProperties cipherProperties) {
        Map<String, Object> properties = merge(propertySources);

        // 解密
        properties.replaceAll((key, value) -> {
            String valueStr = value.toString();
            if (!valueStr.startsWith(ENCRYPTED_PROPERTY_PREFIX)) {
                return value;
            }
            TextEncryptor encryptor = EncryptorFactory.getEncryptor(cipherProperties.getEncrypt());
            return encryptor.decrypt(valueStr.substring(ENCRYPTED_PROPERTY_PREFIX.length()));
        });
        return properties;
    }

    /**
     * 将 {@link PropertySources} 中的属性值合并到一个 {@link Map} 中
     *
     * @param propertySources {@link MutablePropertySources}
     * @return 合并后的键值对
     */
    private Map<String, Object> merge(PropertySources propertySources) {
        Map<String, Object> properties = new LinkedHashMap<>();
        List<PropertySource<?>> sources = new ArrayList<>();
        for (PropertySource<?> source : propertySources) {
            // PropertySources 中 PropertySource 的顺序为优先级顺序
            // 倒序写入 List，便于后续高优先级属性覆盖低优先级属性
            sources.add(0, source);
        }
        for (PropertySource<?> source : sources) {
            merge(source, properties);
        }
        return properties;
    }

    private void merge(PropertySource<?> source, Map<String, Object> properties) {
        if (source instanceof CompositePropertySource) {
            // 递归地处理其嵌套的 PropertySource
            List<PropertySource<?>> sources = new ArrayList<>(((CompositePropertySource) source).getPropertySources());
            Collections.reverse(sources);

            for (PropertySource<?> nested : sources) {
                merge(nested, properties);
            }
        } else if (source instanceof EnumerablePropertySource) {
            Map<String, Object> otherCollectionProperties = new LinkedHashMap<>();
            boolean sourceHasDecryptedCollection = false;

            EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
            for (String key : enumerable.getPropertyNames()) {
                Object property = source.getProperty(key);
                if (property != null) {
                    String value = property.toString();
                    if (value.startsWith(ENCRYPTED_PROPERTY_PREFIX)) {
                        properties.put(key, value);
                        if (COLLECTION_PROPERTY.matcher(key).matches()) {
                            // 有集合属性的元素值是加密的
                            sourceHasDecryptedCollection = true;
                        }
                    } else if (COLLECTION_PROPERTY.matcher(key).matches()) {
                        // 集合属性：放置未加密的属性，以便合并索引属性
                        otherCollectionProperties.put(key, value);
                    } else {
                        // 使用非加密属性覆盖以前加密的属性
                        properties.remove(key);
                    }
                }
            }
            // 复制所有集合属性，即使未加密
            if (sourceHasDecryptedCollection && !otherCollectionProperties.isEmpty()) {
                properties.putAll(otherCollectionProperties);
            }
        }
    }

}
