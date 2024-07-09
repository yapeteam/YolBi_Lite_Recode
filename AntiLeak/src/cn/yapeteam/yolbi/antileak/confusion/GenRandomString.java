package cn.yapeteam.yolbi.antileak.confusion;

import java.util.Random;

public class GenRandomString {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*-=|?";
    private static final Random RANDOM = new Random();

    /**
     * 生成一个指定长度的随机字符串
     * @param length 指定长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return sb.toString();
    }

    /**
     * 生成一个默认长度的随机字符串
     * @return 随机字符串
     */
    public static String generateRandomString() {
        return generateRandomString(32);
    }
}
