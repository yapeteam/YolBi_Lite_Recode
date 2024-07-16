package cn.yapeteam.loader.oauth.util;

public class NullUtils {
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static <T> T requireNotNull(T obj, String name) {
        if (obj == null) {
            throw new NullPointerException(name + " must not be null");
        }
        if (obj instanceof String) {
            if (isBlank((String) obj)) {
                throw new IllegalArgumentException(name + " must not be blank");
            }
        }
        return obj;
    }
}
