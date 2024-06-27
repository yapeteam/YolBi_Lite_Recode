package cn.yapeteam.loader;

import cn.yapeteam.ymixin.annotations.*;
import lombok.val;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Mixin(LaunchClassLoader.class)
public class LaunchClassLoaderMixin extends LaunchClassLoader {
    @Shadow
    private Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();

    public LaunchClassLoaderMixin(URL[] sources) {
        super(sources);
    }

    @Inject(method = "findClass", desc = "(Ljava/lang/String;)Ljava/lang/Class;", target = @Target("HEAD"))
    private void onFindClass(@Local(source = "name", index = 1) String name) {
        if ((
                name.startsWith("cn.yapeteam.yolbi.") ||
                        name.startsWith("javafx.") ||
                        name.startsWith("com.sun.glass.") ||
                        name.startsWith("com.sun.javafx.") ||
                        name.startsWith("com.sun.media.") ||
                        name.startsWith("com.sun.prism.") ||
                        name.startsWith("com.sun.scenario.") ||
                        name.startsWith("com.sun.webkit.")
        ) && !cachedClasses.containsKey(name)) {
            try {
                byte[] bytes = null;
                File injection = new File(System.getProperty("user.home"), ".yolbi/injection.jar");
                val zis = new ZipInputStream(Files.newInputStream(injection.toPath()));
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equals(name.replace(".", "/") + ".class")) {
                        val outStream = new ByteArrayOutputStream();
                        val buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) != -1)
                            outStream.write(buffer, 0, len);
                        outStream.close();
                        bytes = outStream.toByteArray();
                        break;
                    }
                }
                Method method = null;
                for (Method declaredMethod : ClassLoader.class.getDeclaredMethods())
                    if (declaredMethod.getName().equals("defineClass") && declaredMethod.getParameterCount() == 4)
                        method = declaredMethod;
                if (method != null) {
                    method.setAccessible(true);
                    if (bytes != null)
                        cachedClasses.put(name, (Class<?>) method.invoke(this, null, bytes, 0, bytes.length));
                    else System.err.println("Failed to load class: " + name);
                } else System.err.println("Failed to find defineClass method in ClassLoader");
            } catch (Exception ignored) {
            }
        }
    }
}
