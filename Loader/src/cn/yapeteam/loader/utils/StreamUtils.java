package cn.yapeteam.loader.utils;

import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    public static void copyStream(OutputStream os, InputStream is) throws IOException {
        copyStream(os, is, 0);
    }

    public static void copyStream(OutputStream os, InputStream is, int bufsize) throws IOException {
        if (bufsize <= 0) bufsize = 4096;
        int len;
        val bytes = new byte[bufsize];
        while ((len = is.read(bytes)) != -1)
            os.write(bytes, 0, len);
    }

    public static byte[] readStream(InputStream inStream) throws IOException {
        val outStream = new ByteArrayOutputStream();
        val buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }
}
