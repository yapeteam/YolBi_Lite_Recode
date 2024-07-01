package cn.yapeteam.yolbi.antileak.utils.encode;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncode {
    public static String encode(final String plainText, final String key) throws Exception {
        if (key == null) {
            return null;
        }

        if (key.length() != 16) {
            return null;
        }

        String ENCODING = "utf-8";
        String ALGORITHM = "AES";
        SecretKey secretKey = new SecretKeySpec(key.getBytes(ENCODING), ALGORITHM);
        String PATTERN = "AES/ECB/pkcs5padding";
        Cipher cipher = Cipher.getInstance(PATTERN);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(ENCODING));
        return java.util.Base64.getEncoder().encodeToString(encryptData);
    }
}
