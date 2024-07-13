package io.fusion.framework.spring.boot.cipher.properties;

import io.fusion.framework.spring.boot.cipher.core.encryptor.AlgorithmTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import static io.fusion.framework.spring.boot.cipher.properties.CipherProperties.PREFIX;

/**
 * @author enhao
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class CipherProperties {

    public static final String PREFIX = "fusion.cipher";

    private boolean enabled = true;

    /**
     * 加密配置
     * <p>
     * NOTE: 强烈建议密钥信息通过环境变量或 JVM 参数配置，不要和加密结果配置在同一个配置文件中
     */
    private Encrypt encrypt = new Encrypt();

    @Data
    public static class Encrypt {

        /**
         * 加密算法
         */
        private AlgorithmTypeEnum algorithm = AlgorithmTypeEnum.AES;

        /**
         * 对称加密算法密钥 或 非对称加密算法私钥
         */
        private String secretKey;

        /**
         * 非对称加密算法私钥和 keyStore 二选一，优先 keyStore
         */
        private KeyStoreConfig keyStore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyStoreConfig {
        /**
         * 密钥库文件的位置
         */
        private Resource location;

        /**
         * 锁定密钥库的密码
         */
        private String password;

        /**
         * 存储中密钥的别名
         */
        private String alias;

        /**
         * 保护密钥的密码（默认为与密码相同）
         */
        private String secret;

        /**
         * KeyStore 类型。默认为 jks
         */
        private String type = "jks";

        public String getSecret() {
            return this.secret == null ? this.password : this.secret;
        }
    }
}
