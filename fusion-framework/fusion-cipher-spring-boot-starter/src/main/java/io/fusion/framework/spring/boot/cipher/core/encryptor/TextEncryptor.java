package io.fusion.framework.spring.boot.cipher.core.encryptor;

/**
 * 文本加密器接口
 *
 * @author enhao
 */
public interface TextEncryptor {

    /**
     * 加密
     *
     * @param plainText 需加密的文本
     * @return 加密后的文本
     */
    String encrypt(String plainText);

    /**
     * 解密
     *
     * @param cipherText 加密后的文本
     * @return 解密后的文本
     */
    String decrypt(String cipherText);
}
