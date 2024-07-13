package io.fusion.framework.spring.boot.cipher.core.encryptor;

import io.fusion.framework.spring.boot.cipher.properties.CipherProperties;

/**
 * 加密器工厂
 *
 * @author enhao
 */
public class EncryptorFactory {

    /**
     * 获取加密器
     *
     * @param config 加密配置
     * @return {@link TextEncryptor} 实现类
     */
    public static TextEncryptor getEncryptor(CipherProperties.Encrypt config) {
        switch (config.getAlgorithm()) {
            case AES:
                return new AesCryptoEncryptor(config.getSecretKey());
            case RSA:
                if (config.getKeyStore() != null) {
                    return new RsaKeyStoreEncryptor(config.getKeyStore());
                }
                return new RsaCryptoEncryptor(config.getSecretKey());
            default:
                throw new IllegalArgumentException("Unsupported encryptor type: " + config.getAlgorithm());
        }
    }
}
