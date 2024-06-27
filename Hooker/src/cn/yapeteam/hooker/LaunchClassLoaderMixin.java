package cn.yapeteam.hooker;

import cn.yapeteam.ymixin.annotations.*;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(LaunchClassLoader.class)
public class LaunchClassLoaderMixin extends LaunchClassLoader {
    @Shadow
    private Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();

    public LaunchClassLoaderMixin(URL[] sources) {
        super(sources);
    }

    @Inject(method = "findClass", desc = "(Ljava/lang/String;)Ljava/lang/Class;", target = @Target("HEAD"))
    private void onFindClass(@Local(source = "name", index = 1) String name) {
        try {
            if (Hooker.shouldHook(name) && cachedClasses.get(name) == null) {
                System.out.println("Finding class: " + name);
                if (name.startsWith("cn.yapeteam.yolbi.") && !Hooker.cachedInjection) {
                    Hooker.cachedInjection = true;
                    Hooker.cacheJar(new File(Hooker.YOLBI_DIR, "injection.jar"));
                }
                byte[] bytes = Hooker.classes.get(name);
                if (bytes != null)
                    cachedClasses.put(name, (Class<?>) Hooker.defineClass.invoke(this, name, bytes, 0, bytes.length));
                else System.err.println("Failed to load class: " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
