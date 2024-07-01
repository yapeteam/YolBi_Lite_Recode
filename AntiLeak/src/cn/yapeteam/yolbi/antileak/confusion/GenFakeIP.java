package cn.yapeteam.yolbi.antileak.confusion;

import java.util.Random;

public class GenFakeIP {
    private static final Random random = new Random();

    public static String generateRandomIP(int length) {
        if (length < 1 || length > 4) {
            return null;
        }

        StringBuilder ip = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int octet = random.nextInt(256);
            ip.append(octet);
            if (i < length - 1) {
                ip.append(".");
            }
        }

        return ip.toString();
    }
}
