package sj.sj_troubleshooting.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Utility class to generate RSA key pairs for JWT signing
 * This can be used to generate new keys if needed
 */
public class RsaKeyGenerator {

    public static void main(String[] args) {
        try {
            KeyPair keyPair = generateRsaKeyPair();
            System.out.println("=== PRIVATE KEY ===");
            System.out.println(formatPrivateKey(keyPair.getPrivate()));
            System.out.println("\n=== PUBLIC KEY ===");
            System.out.println(formatPublicKey(keyPair.getPublic()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // 2048-bit key size
        return keyPairGenerator.generateKeyPair();
    }

    private static String formatPrivateKey(PrivateKey privateKey) {
        String encoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" + 
               formatKeyString(encoded) + 
               "\n-----END PRIVATE KEY-----";
    }

    private static String formatPublicKey(PublicKey publicKey) {
        String encoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + 
               formatKeyString(encoded) + 
               "\n-----END PUBLIC KEY-----";
    }

    private static String formatKeyString(String key) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < key.length(); i += 64) {
            if (i + 64 < key.length()) {
                formatted.append(key.substring(i, i + 64)).append("\n");
            } else {
                formatted.append(key.substring(i));
            }
        }
        return formatted.toString();
    }
}
