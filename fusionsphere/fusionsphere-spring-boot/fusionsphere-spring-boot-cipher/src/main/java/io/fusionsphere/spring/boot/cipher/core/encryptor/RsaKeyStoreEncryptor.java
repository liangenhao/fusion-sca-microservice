package io.fusionsphere.spring.boot.cipher.core.encryptor;

import io.fusionsphere.spring.boot.cipher.properties.CipherProperties;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

/**
 * 使用 KeyStore 的 RSA 加密
 *
 * @author enhao
 */
public class RsaKeyStoreEncryptor extends AbstractCryptoEncryptor {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public RsaKeyStoreEncryptor(CipherProperties.KeyStoreConfig config) {
        Assert.notNull(config.getLocation(), () -> "KeyStore location must not be null");
        Assert.hasText(config.getPassword(), () -> "KeyStore password must not be empty");
        Assert.hasText(config.getAlias(), () -> "KeyStore alias must not be empty");
        Assert.hasText(config.getType(), () -> "KeyStore type must not be empty");
        try {
            KeyStore keyStore = KeyStore.getInstance(config.getType());
            try (InputStream stream = config.getLocation().getInputStream()) {
                keyStore.load(stream, config.getPassword().toCharArray());
            }
            this.privateKey = (PrivateKey) keyStore.getKey(config.getAlias(), config.getSecret().toCharArray());
            Certificate certificate = keyStore.getCertificate(config.getAlias());
            if (certificate != null) {
                this.publicKey = certificate.getPublicKey();
            } else {
                RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
                RSAPublicKeySpec spec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent());
                this.publicKey = KeyFactory.getInstance(getAlgorithm().name()).generatePublic(spec);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load keys from store: " + config.getLocation(), e);
        }
    }

    @Override
    protected AlgorithmTypeEnum getAlgorithm() {
        return AlgorithmTypeEnum.RSA;
    }

    @Override
    public String encrypt(String plainText) {
        return encrypt(plainText, publicKey);
    }

    @Override
    public String decrypt(String cipherText) {
        return decrypt(cipherText, privateKey);
    }
}
