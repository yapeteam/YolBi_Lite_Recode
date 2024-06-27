package cn.yapeteam.hooker;

import cn.yapeteam.ymixin.Transformer;
import cn.yapeteam.ymixin.YMixin;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.utils.Mapper;
import lombok.val;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static cn.yapeteam.ymixin.utils.ASMUtils.node;
import static cn.yapeteam.ymixin.utils.ASMUtils.rewriteClass;

@SuppressWarnings("unused")
public class Hooker {
    public static final String YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi").getAbsolutePath();

    public static boolean shouldHook(String name) {
        return name.startsWith("cn.yapeteam.") ||
                name.startsWith("org.objectweb.") ||
                name.startsWith("com.formdev.") ||
                name.startsWith("javafx.") ||
                name.startsWith("com.sun.glass.") ||
                name.startsWith("com.sun.javafx.") ||
                name.startsWith("com.sun.media.") ||
                name.startsWith("com.sun.prism.") ||
                name.startsWith("com.sun.scenario.") ||
                name.startsWith("com.sun.webkit.");
    }

    private static byte[] readStream(InputStream inStream) throws Exception {
        val outStream = new ByteArrayOutputStream();
        val buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }

    private static byte[] getClassFindHook() throws Throwable {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(new File(YOLBI_DIR, "hooker.jar").toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    if (entry.getName().equals("cn/yapeteam/hooker/LaunchClassLoaderMixin.class")) {
                        return readStream(zis);
                    }
                }
            }
        }
        return null;
    }


    private native static byte[] getClassBytes(Class<?> clazz);

    private native static int redefineClass(Class<?> clazz, byte[] bytes);

    public static Method defineClass;

    public static Thread client_thread = null;
    public static final Map<String, byte[]> classes = new HashMap<>();
    public static boolean cachedInjection = false;

    public static void cacheJar(File file) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
                if (!entry.isDirectory())
                    if (entry.getName().endsWith(".class"))
                        classes.put(entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6), readStream(zis));
        }
    }

    public static void hook() {
        try {
            cacheJar(new File(YOLBI_DIR, "loader.jar"));
        } catch (Exception ignored) {
        }
        try {
            cacheJar(new File(YOLBI_DIR, "deps.jar"));
        } catch (Exception ignored) {
        }
        for (Method declaredMethod : ClassLoader.class.getDeclaredMethods()) {
            if (declaredMethod.getName().equals("defineClass") && declaredMethod.getParameterCount() == 4) {
                defineClass = declaredMethod;
                break;
            }
        }
        if (defineClass == null)
            throw new RuntimeException("Failed to find defineClass method.");
        System.out.println("Starting hooker...");
        for (Object o : Thread.getAllStackTraces().keySet().toArray()) {
            Thread thread = (Thread) o;
            if (thread.getName().equals("Client thread")) {
                client_thread = thread;
                break;
            }
        }

        System.out.println("Initializing YMixin...");
        Mapper.setMode(Mapper.Mode.None);
        YMixin.init(
                name -> {
                    try {
                        return Class.forName(name.replace("/", "."), true, client_thread.getContextClassLoader());
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                }, null
        );

        System.out.println("Hooking LaunchClassLoader...");
        try {
            Class.forName("net.minecraft.launchwrapper.LaunchClassLoader", true, client_thread.getContextClassLoader());

            try {
                System.out.println("Transforming LaunchClassLoader...");
                Transformer transformer = new Transformer(Hooker::getClassBytes);
                System.out.println("Transforming LaunchClassLoader mixin...");
                byte[] classFindHook = rewriteClass(Objects.requireNonNull(ShadowTransformer.transform(Objects.requireNonNull(node(getClassFindHook())))));
                if (classFindHook == null)
                    System.out.println("Transforming LaunchClassLoader mixin failed.");
                System.out.println("Transforming LaunchClassLoader mixin done.");
                ClassNode classFindHookNode = node(classFindHook);
                if (classFindHookNode == null)
                    System.out.println("Transforming LaunchClassLoader mixin failed.");
                System.out.println("Transforming LaunchClassLoader...");
                Class<?> LaunchClassLoaderClass = Objects.requireNonNull(Mixin.Helper.getAnnotation(classFindHookNode)).value();
                System.out.println("Transforming LaunchClassLoader done.");
                transformer.addMixin(classFindHookNode);
                System.out.println("Transforming LaunchClassLoader done.");
                val bytes = transformer.transform().get(LaunchClassLoaderClass.getName());
                System.out.println("Hooked LaunchClassLoader, return code: " + redefineClass(LaunchClassLoaderClass, bytes));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (
                ClassNotFoundException ignored) {
            System.out.println("LaunchClassLoader not found, skipping hook.");
        }
    }
}
