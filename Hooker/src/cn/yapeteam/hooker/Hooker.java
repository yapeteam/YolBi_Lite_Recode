package cn.yapeteam.hooker;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm_9_2.ClassReader;
import org.objectweb.asm_9_2.ClassWriter;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.objectweb.asm_9_2.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm_9_2.ClassWriter.COMPUTE_MAXS;

/**
 * native used
 * native invoked
 */
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

    private native static byte[] getClassBytes(Class<?> clazz);

    public native static Class<?> defineClass(ClassLoader loader, byte[] bytes);

    private native static int redefineClass(Class<?> clazz, byte[] bytes);

    public static Thread client_thread = null;
    public static final Map<String, byte[]> classes = new HashMap<>();
    public static final Map<String, Class<?>> cachedClasses = new HashMap<>();

    private static Class<?> getClass(String name) {
        try {
            return Class.forName(name, true, client_thread.getContextClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static void cacheJar(File file) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
                if (!entry.isDirectory())
                    if (entry.getName().endsWith(".class"))
                        classes.put(entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6), readStream(zis));
        }
    }

    private static ClassNode node(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            ClassReader reader = new ClassReader(bytes);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            return node;
        }

        return null;
    }

    private static byte[] rewriteClass(@NotNull ClassNode node) {
        ClassWriter writer = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES) {
            @Override
            protected @NotNull String getCommonSuperClass(@NotNull String type1, @NotNull String type2) {
                try {
                    Class<?> class1 = Hooker.getClass(type1);
                    Class<?> class2 = Hooker.getClass(type2);
                    if (class1 != null && class2 != null) {
                        if (class1.isAssignableFrom(class2)) {
                            return type1;
                        } else if (class2.isAssignableFrom(class1)) {
                            return type2;
                        } else if (!class1.isInterface() && !class2.isInterface()) {
                            do {
                                class1 = class1.getSuperclass();
                            } while (!class1.isAssignableFrom(class2));
                            return class1.getName().replace('.', '/');
                        }
                    }
                } catch (Throwable ignored) {
                }
                return "java/lang/Object";
            }
        };
        node.accept(writer);
        return writer.toByteArray();
    }

    @SuppressWarnings("unused")
    public static Class<?> onFindClass(ClassLoader cl, String name) {
        try {
            if (shouldHook(name)) {
                if (name.startsWith("cn.yapeteam.yolbi.") && !classes.containsKey(name))
                    Hooker.cacheJar(new File(Hooker.YOLBI_DIR, "injection.jar"));
                if (name.startsWith("javafx.") && !classes.containsKey(name)) {
                    if (System.getProperty("java.version").startsWith("1.8"))
                        Hooker.cacheJar(new File(Hooker.YOLBI_DIR, "dependencies/jfxrt/jfxrt.jar"));
                    else
                        for (File file : Objects.requireNonNull(new File(Hooker.YOLBI_DIR, "dependencies/javafx").listFiles()))
                            Hooker.cacheJar(file);
                }
                byte[] bytes = Hooker.classes.get(name);
                if (bytes == null) {
                    System.out.println("Failed to find class: " + name);
                    throw new RuntimeException(name);
                }
                Class<?> clz = cachedClasses.get(name);
                if (clz != null)
                    return clz;
                clz = defineClass(cl, bytes);
                cachedClasses.put(name, clz);
                return clz;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressWarnings("unused")
    public static void hook() {
        try {
            cacheJar(new File(YOLBI_DIR, "ymixin.jar"));
        } catch (Exception ignored) {
        }
        try {
            cacheJar(new File(YOLBI_DIR, "loader.jar"));
        } catch (Exception ignored) {
        }
        try {
            for (File file : Objects.requireNonNull(new File(YOLBI_DIR, "dependencies").listFiles())) {
                cacheJar(file);
            }
        } catch (Exception ignored) {
        }
        for (Object o : Thread.getAllStackTraces().keySet().toArray()) {
            Thread thread = (Thread) o;
            if (thread.getName().equals("Client thread")) {
                client_thread = thread;
                break;
            }
        }

        boolean hasLaunchClassLoader = true;
        try {
            Class.forName("net.minecraft.launchwrapper.LaunchClassLoader", true, client_thread.getContextClassLoader());
        } catch (ClassNotFoundException e) {
            hasLaunchClassLoader = false;
        }
        if (hasLaunchClassLoader) {
            try {
                byte[] targetBytes = getClassBytes(client_thread.getContextClassLoader().getClass());
                ClassNode targetNode = node(targetBytes);
                for (MethodNode method : targetNode.methods) {
                    if (method.name.equals("findClass") && method.desc.equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
                        LabelNode labelNode = new LabelNode();
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hooker.class),
                                "onFindClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;"));
                        insnList.add(new VarInsnNode(Opcodes.ASTORE, 2));
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
                        insnList.add(new JumpInsnNode(Opcodes.IFNULL, labelNode));
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
                        insnList.add(new InsnNode(Opcodes.ARETURN));
                        insnList.add(labelNode);
                        method.instructions.insert(insnList);
                        break;
                    }
                }
                val bytes = rewriteClass(targetNode);
                redefineClass(client_thread.getContextClassLoader().getClass(), bytes);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
