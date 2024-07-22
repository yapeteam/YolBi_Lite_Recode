package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    public static class resources {
        public static final Map<String, byte[]> res = new HashMap<>();

        public static InputStream getStream(String name) {
            if (res.containsKey(name))
                return new ByteArrayInputStream(res.get(name));
            File file = new File(Loader.YOLBI_DIR, "resources/" + name);
            try {
                if (file.exists())
                    return Files.newInputStream(file.toPath());
            } catch (IOException e) {
                Logger.exception(e);
            }
            InputStream stream = ResourceManager.class.getResourceAsStream("/" + name);
            if (stream != null)
                return stream;
            Logger.exception(new RuntimeException("Resource not found: " + name));
            return null;
        }

        public static boolean isFile(String path) {
            return new File(Loader.YOLBI_DIR, "resources/" + path).isFile();
        }

        public static byte[] get(String name) {
            InputStream stream = getStream(name);
            if (stream != null) {
                try {
                    return StreamUtils.readStream(stream);
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
            return null;
        }
    }
}
