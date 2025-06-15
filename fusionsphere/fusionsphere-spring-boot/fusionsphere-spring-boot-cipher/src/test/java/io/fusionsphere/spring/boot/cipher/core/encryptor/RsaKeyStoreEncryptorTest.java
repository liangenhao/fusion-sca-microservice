package io.fusionsphere.spring.boot.cipher.core.encryptor;

import io.fusionsphere.spring.boot.cipher.properties.CipherProperties;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author enhao
 */
public class RsaKeyStoreEncryptorTest {

    @Test
    public void testGenerateKeyStore() throws Exception {
        FileSystemResource resource = new FileSystemResource("/Users/enhao/Documents/projects/personal/fusion-sca-microservice/scripts/keystore-own.jks");
        CipherProperties.KeyStoreConfig config = CipherProperties.KeyStoreConfig.builder()
                .location(resource)
                .password("qazwsx")
                .alias("mykey")
                .type("jks")
                .build();
        generateKeyStore(config, 2048, 365 * 100, "enhao", "fusion", "fusion", "nanjing", "jiangsu", "China");
    }

    @Test
    public void testEncrypt() {
        FileSystemResource resource = new FileSystemResource("/Users/enhao/Documents/projects/personal/fusion-sca-microservice/scripts/keystore-own.jks");
        CipherProperties.KeyStoreConfig config = CipherProperties.KeyStoreConfig.builder()
                .location(resource)
                .password("qazwsx")
                .alias("mykey")
                .type("jks")
                .build();
        String plainText = "enhao";
        System.out.println("KeyStore: " + config);
        System.out.println("Plain Text: " + plainText);
        RsaKeyStoreEncryptor encryptor = new RsaKeyStoreEncryptor(config);
        String encrypted = encryptor.encrypt(plainText);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("---");
        String decrypted = encryptor.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }

    /**
     * 生成密钥库
     *
     * @param config   密钥库配置
     * @param keySize  密钥长度
     * @param validity 有效期，单位天
     * @throws Exception
     */
    private void generateKeyStore(CipherProperties.KeyStoreConfig config, int keySize, long validity,
                                  String commonName, String orgUnitName, String orgName, String localityName,
                                  String stateName, String countryName) throws Exception {
        KeyStore ks = KeyStore.getInstance(config.getType());
        ks.load(null, null);

        CertAndKeyGen keypair = new CertAndKeyGen("RSA", "SHA1WithRSA", null);
        X500Name x500Name = new X500Name(commonName, orgUnitName, orgName, localityName, stateName, countryName);
        keypair.generate(keySize);

        PrivateKey privateKey = keypair.getPrivateKey();
        X509Certificate[] chain = new X509Certificate[1];
        chain[0] = keypair.getSelfCertificate(x500Name, new Date(), validity * 24 * 60 * 60);

        try (FileOutputStream fos = new FileOutputStream(config.getLocation().getFile())) {
            ks.setKeyEntry(config.getAlias(), privateKey, config.getSecret().toCharArray(), chain);
            ks.store(fos, config.getPassword().toCharArray());
        }
    }

}
