package io.fusion.framework.spring.boot.cipher.core.encryptor;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Base64;

/**
 * java crypto 加密器
 *
 * @author enhao
 */
public abstract class AbstractCryptoEncryptor extends AbstractEncryptor {

    /**
     * 加密
     *
     * @param plainText 明文
     * @param secretKey 密钥
     * @return 密文
     */
    protected String encrypt(String plainText, Key secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(getAlgorithm().name());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting", e);
        }
    }

    /**
     * 解密
     *
     * @param cipherText 密文
     * @param secretKey  密钥
     * @return 明文
     */
    protected String decrypt(String cipherText, Key secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(getAlgorithm().name());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting", e);
        }
    }
}
