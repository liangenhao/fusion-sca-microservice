package io.fusionsphere.spring.boot.cipher.core.encryptor;

import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AES 加密器
 *
 * @author enhao
 */
public class AesCryptoEncryptor extends AbstractCryptoEncryptor {

    private final SecretKeySpec secretKey;

    public AesCryptoEncryptor(String secretKey) {
        Assert.hasText(secretKey, () -> "'secretKey' must not be empty");
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.secretKey = new SecretKeySpec(decodedKey, getAlgorithm().name());
    }


    @Override
    protected AlgorithmTypeEnum getAlgorithm() {
        return AlgorithmTypeEnum.AES;
    }

    @Override
    public String encrypt(String plainText) {
        return encrypt(plainText, secretKey);
    }

    @Override
    public String decrypt(String cipherText) {
        return decrypt(cipherText, secretKey);
    }

}