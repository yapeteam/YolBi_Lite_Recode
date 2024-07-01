package cn.yapeteam.yolbi.utils.file;

import cn.yapeteam.loader.ResourceManager;

import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static byte[] readBinaryPath(String path) {
        return ResourceManager.resources.get(path);
    }

    public static String readPath(String resourcePath) {
        return new String(readBinaryPath(resourcePath), StandardCharsets.UTF_8);
    }
}
