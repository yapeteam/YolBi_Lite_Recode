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
        System.out.println("Finding class: " + name);
        if ((
                name.startsWith("cn.yapeteam.yolbi.") ||
                        name.startsWith("javafx.")
        ) && !cachedClasses.containsKey(name)) {
            try {
                byte[] classBytes = null;
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
                        classBytes = outStream.toByteArray();
                        break;
                    }
                }
                Method method = null;
                for (Method declaredMethod : ClassLoader.class.getDeclaredMethods())
                    if (declaredMethod.getName().equals("defineClass") && declaredMethod.getParameterCount() == 4)
                        method = declaredMethod;
                method.setAccessible(true);
                Class<?> clazz;
                if (classBytes != null) {
                    clazz = (Class<?>) method.invoke(this, name, classBytes, 0, classBytes.length);
                    cachedClasses.put(name, clazz);
                    System.out.println("Loaded class: " + clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
