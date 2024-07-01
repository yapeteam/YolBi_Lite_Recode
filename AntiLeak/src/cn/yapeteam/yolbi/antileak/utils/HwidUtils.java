package cn.yapeteam.yolbi.antileak.utils;

import cn.yapeteam.yolbi.antileak.AntiLeak;
import cn.yapeteam.yolbi.antileak.confusion.GenRandomString;
import cn.yapeteam.yolbi.antileak.utils.encode.AESEncode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HwidUtils {
    private static String hwid = GenRandomString.generateRandomString();

    public static void getHwid() throws Exception {
        StringBuilder s = new StringBuilder();
        String main = "Yolbi" + System.getenv("COMPUTERNAME") + System.getProperty("user.name");
        byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] sha = messageDigest.digest(bytes);

        for (final byte b : sha) {
            s.append(Integer.toHexString((b & 0xFF) | 0x300), 0, 3);
        }

        hwid = AESEncode.encode((s).substring(s.length() - 20, s.length()), AntiLeak.instance.ENCODE_KEY);
    }
}
