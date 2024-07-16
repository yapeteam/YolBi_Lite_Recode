package cn.yapeteam.loader.oauth.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class EncryptionUtil {
    private static Cipher cipher;
    private static SecureRandom random;

    public static String key = System.getProperty("user.home") + "cn/yapeteam/loader/oauth";

    static {
        try {
            cipher = Cipher.getInstance("AES/CTR/NoPadding");
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encryptString(String s, String key) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(hashPassword(key), 0, 16);
            SecretKeySpec keySpec = new SecretKeySpec(hashPassword(key), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return new String(cipher.doFinal(Base64.getEncoder().encode(s.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decryptString(String s, String key) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(hashPassword(key), 0, 16);
            SecretKeySpec keySpec = new SecretKeySpec(hashPassword(key), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(s)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] hashPassword(String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(pass.toCharArray(), "cn/yapeteam/loader/oauth".getBytes(), 65536, 256)).getEncoded();
    }
}
