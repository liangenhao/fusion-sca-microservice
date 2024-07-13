package io.fusion.framework.spring.boot.cipher.core.encryptor;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Base64;

/**
 * @author enhao
 */
public class RsaCryptoEncryptorTest {


    @Test
    public void testGenerateKeyPair() throws Exception {
        KeyPairInfo keyPairInfo = generateKeyPair(2048);
        System.out.println("Public Key: " + keyPairInfo.getPublicKey());
        System.out.println("Private Key: " + keyPairInfo.getPrivateKey());
    }

    @Test
    public void testEncrypt() {
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDhCCvG9xJkrIeVsdE4i50v15KujX+dQJCzILvzjO3SrpRBDJRlhSLIEjg3boNxOMlkiV7ZU7t/I4eypatWi2FQpIx+Bhn8rVCAvp+vxBLDhfcneqPdlIjokUtqfMJhvlAeOZij24xJuBYAMeuyFImZbAXHtEzgMN1EXT1rW7GoQt9kA+5EeVvDcmyZ+SLgvgnb5iPLwaLfcXzzJ/6771qlj8m/PiNycvDDmubWh0216f/mawkLiYseST2ZzyDpxaTZxMVKXWtwnb+T0Qt7H6x4mZMSbigHzsEnE/c4ljD63RE3vG6SkkVWEB7UXZkBlUFYUxCLOmdiXwnWKYRL4Y+hAgMBAAECggEAVOW1RmIuUUb5/BLS4SdjDQqibzI5BAW40au23QK9Mq/khMoPLmHVJnAhP2B1PID1EfX1j75UTXoFFvQDuSnqc/cwfdcldLuaVIgWTMSHY7al8QIG0nDYWg1+y1T4LNIU/eIy4RbSTXmi6z3qMwwmgXhMkfRG0In+4Gzg4CD+3IBxDvn8WypsMXjH5GvsjhfDD1x7mNoKlm+S6pGW6QNipdtSt9vtdP4ew56nmVad9Ep3CXPUmy3hjjayT4Y6OSkd2vx1uIdPXj8udtb/wwYcysGqWMi1bTyYUdAu3fjk8D4oRSg45tomyulGitf+9LzMMZjxk/OxguS12j8uVEpIAQKBgQD0W2fwgCp50/DNP7D8PBdvfItEK0NfrF3qmCZot7GCUJOeyKjlsGrQiTk0ah650yXw9HSMXNIKvspKoIYxaMjl9MrB3PbdmvWfbiwzyg+iVNtJt5UYW6d7U+W7rPFz/uKAY8jBHjoMmLe8CC9j9QqhsAHVmpISQl/jWKNNFFo4wQKBgQDrwQrzVBr0Pgp3Uon92UFxo5TduowfLwlpL9s1ouQnHLuNt31x2WT+EsDA+IPgu7zZQtal8VF/Zha2B0nEOHcQ/+9UydGye0rpl5gUj9vo37lOJOIBOrdHf37HYjBh0RzXl/IqT7D38b7f2cUDhMtOHdJ8hdAbfTiIxRmzcCMu4QKBgGgUDr9nN7BQT358pFurFKhNudu9OaMkfZQQXju4EglZXKcC/L1oWPadnBnxIUkw4DYzSPkICAa/oNYMbVvaL8eSblIIVbWp7PiNKpKce/A+iHBrmzuWvk1PPMilLuqjI8JfvgBTaZI38fpK2jSPCIo5t25Nue3BC6p/9H4+LveBAoGBANoxNSBVlAi/LtKWqq4tl/tD6bB5SdxGnVAkAZ8Xnk9wsUkNLOXkzSq1Un6Gww+I0dTGnoDA4qulYZmhDy2zEMfEB86SZyjoIUImkpNdPau++/MOAbofjKd4oW6JOrwdXLNQpslwLhqcPAtTspp6Geu3gCrNcfkvINa8jPfc7kMhAoGANhTeTMqLiMSwL7RVnLe96qfoS+Rl7YUtZrAxEN4jlGBvyuRMcajpS8cQ2kcjkiC4Yl02m/EKXSiFWm6ZVG7Mo+57z6YyNlAR0KAjJEMsus8dBGLMc/eP1FfxTxt50OO4OCoyzo4UqoYPd9tdu+gMJszuR/RGFwJgoJzYybBwIFA=";
        String plainText = "enhao";
        System.out.println("Private Key: " + privateKey);
        System.out.println("Plain Text: " + plainText);
        RsaCryptoEncryptor encryptor = new RsaCryptoEncryptor(privateKey);
        String encrypted = encryptor.encrypt(plainText);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("---");
        String decrypted = encryptor.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }

    private KeyPairInfo generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize);
        KeyPair keyPair = keyGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String base64PublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String base64PrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        return new KeyPairInfo(base64PublicKey, base64PrivateKey);
    }


    @Data
    public static class KeyPairInfo {
        String privateKey;
        String publicKey;

        public KeyPairInfo(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }
}
