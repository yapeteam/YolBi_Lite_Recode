package cn.yapeteam.yolbi.utils.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLUtil {
    public static String encode(String original) {
        try {
            return URLEncoder.encode(original, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String original) {
        try {
            return URLDecoder.decode(original, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getValues(HttpExchange he) {
        String[] keyValues = he.getRequestURI().getRawQuery().split("&");
        String[] values = new String[keyValues.length];
        for (int i = 0; i < keyValues.length; i++) {
            values[i] = URLUtil.decode(keyValues[i].split("=")[1]);
        }
        return values;
    }
}
