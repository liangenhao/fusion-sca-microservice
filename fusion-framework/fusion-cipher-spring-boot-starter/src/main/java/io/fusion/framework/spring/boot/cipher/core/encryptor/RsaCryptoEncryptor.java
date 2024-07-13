package io.fusion.framework.spring.boot.cipher.core.encryptor;

import lombok.SneakyThrows;
import org.springframework.util.Assert;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * RSA 加密器
 *
 * @author enhao
 */
public class RsaCryptoEncryptor extends AbstractCryptoEncryptor {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    @SneakyThrows
    public RsaCryptoEncryptor(String privateKey) {
        Assert.hasText(privateKey, () -> "'secretKey' must not be empty");
        byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
        this.privateKey = KeyFactory.getInstance(getAlgorithm().name()).generatePrivate(privateKeySpec);

        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) this.privateKey;
        RSAPublicKeySpec spec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent());
        this.publicKey = KeyFactory.getInstance(getAlgorithm().name()).generatePublic(spec);
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
