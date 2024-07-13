package io.fusion.framework.spring.boot.cipher.core.encryptor;

import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author enhao
 */
public class AesCryptoEncryptorTest {


    @Test
    public void testGenerateKey() throws Exception {
        String key128 = generateKey(128);
        System.out.println("Generated 128-bit AES Key: " + key128);

        // 生成192位AES密钥
        String key192 = generateKey(192);
        System.out.println("Generated 192-bit AES Key: " + key192);

        // 生成256位AES密钥
        String key256 = generateKey(256);
        System.out.println("Generated 256-bit AES Key: " + key256);
    }

    @Test
    public void testEncrypt() {
        String secretKey = "alqcVQw/LtxxD650FfQ2Cg==";
        String plainText = "enhao";
        System.out.println("Secret Key: " + secretKey);
        System.out.println("Plain Text: " + plainText);
        AesCryptoEncryptor encryptor = new AesCryptoEncryptor(secretKey);
        String encrypted = encryptor.encrypt(plainText);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("---");
        String decrypted = encryptor.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }

    public String generateKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
