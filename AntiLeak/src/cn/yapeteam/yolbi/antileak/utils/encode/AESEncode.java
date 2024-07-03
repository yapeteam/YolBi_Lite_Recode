package cn.yapeteam.yolbi.antileak.utils.encode;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESEncode {
    /**
     * 加密函数
     * @param plainText 明文
     * @param key 密钥
     * @return 密文
     * @throws Exception
     */
    public static String encode(final String plainText, final String key) throws Exception {
        // 如果密钥为空，则返回null
        if (key == null) {
            return null;
        }

        // 如果密钥长度不为16，则返回null
        if (key.length() != 16) {
            return null;
        }

        // 定义编码格式
        String ENCODING = "utf-8";
        // 定义加密算法
        String ALGORITHM = "AES";
        // 将密钥转换为字节数组
        SecretKey secretKey = new SecretKeySpec(key.getBytes(ENCODING), ALGORITHM);
        // 定义加密模式
        String PATTERN = "AES/ECB/pkcs5padding";
        // 获取加密实例
        Cipher cipher = Cipher.getInstance(PATTERN);
        // 初始化加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        // 对明文进行加密
        byte[] encryptData = cipher.doFinal(plainText.getBytes(ENCODING));
        // 将加密后的字节数组转换为Base64编码的字符串
        return Base64.getEncoder().encodeToString(encryptData);
    }
}
