package io.fusionsphere.spring.boot.cipher.core.encryptor;

/**
 * {@link TextEncryptor} 抽象实现
 *
 * @author enhao
 */
public abstract class AbstractEncryptor implements TextEncryptor {

    /**
     * 算法类型
     *
     * @return 算法类型
     */
    protected abstract AlgorithmTypeEnum getAlgorithm();
}
