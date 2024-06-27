package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ASMUtils;
import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.ymixin.Transformer;
import cn.yapeteam.ymixin.YMixin;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.utils.Mapper;
import lombok.val;
import org.objectweb.asm_9_2.tree.ClassNode;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("unused")
public class BootStrap {
    private static native void loadInjection();

    private static boolean initialized = false;

    public static void initHook() {
        if (!initialized) {
            initialized = true;
            new Thread(() -> {
                Loader.preload();
                loadInjection();
            }).start();
        }
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
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(new File(Loader.YOLBI_DIR, "loader.jar").toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = entry.getName().replace('/', '.');
                    name = name.substring(0, name.length() - 6);
                    if (name.equals(LaunchClassLoaderMixin.class.getName())) return readStream(zis);
                }
            }
        }
        return null;
    }

    private static byte[] getInitHook() throws Throwable {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(new File(Loader.YOLBI_DIR, "loader.jar").toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = entry.getName().replace('/', '.');
                    name = name.substring(0, name.length() - 6);
                    if (name.equals(InitHookMixin.class.getName())) return readStream(zis);
                }
            }
        }
        return null;
    }

    public static Mapper.Mode guessMappingMode() {
        Class<?> clazz = ClassUtils.getClass("net.minecraft.client.Minecraft");
        if (clazz == null) return Mapper.Mode.Vanilla;
        byte[] bytes = JVMTIWrapper.instance.getClassBytes(clazz);
        ClassNode node = cn.yapeteam.ymixin.utils.ASMUtils.node(bytes);
        if (node.methods.stream().anyMatch(m -> m.name.equals("runTick")))
            return Mapper.Mode.None;
        return Mapper.Mode.Searge;
    }

    public static Thread client_thread = null;

    public static void entry() throws Throwable {
        if (JVMTIWrapper.instance == null)
            JVMTIWrapper.instance = new NativeWrapper();
        for (Object o : Thread.getAllStackTraces().keySet().toArray()) {
            Thread thread = (Thread) o;
            if (thread.getName().equals("Client thread")) {
                client_thread = thread;
                UIManager.getDefaults().put("ClassLoader", thread.getContextClassLoader());
                break;
            }
        }
        Mapper.Mode mode = guessMappingMode();
        YMixin.init(
                name -> {
                    try {
                        return Class.forName(name.replace("/", "."), true, client_thread.getContextClassLoader());
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                }, new cn.yapeteam.ymixin.Logger() {
                    @Override
                    public void error(String str, Object... o) {
                        Logger.error(str, o);
                    }

                    @Override
                    public void info(String str, Object... o) {
                        Logger.info(str, o);
                    }

                    @Override
                    public void warn(String str, Object... o) {
                        Logger.warn(str, o);
                    }

                    @Override
                    public void success(String str, Object... o) {
                        Logger.success(str, o);
                    }

                    @Override
                    public void exception(Throwable ex) {
                        Logger.exception(ex);
                    }
                }
        );
        Logger.info("Reading mappings, mode: {}", mode.name());
        Mapper.setMode(mode);
        String vanilla = new String(Objects.requireNonNull(ResourceManager.resources.get("mappings/vanilla.srg")), StandardCharsets.UTF_8);
        String forge = new String(Objects.requireNonNull(ResourceManager.resources.get("mappings/forge.srg")), StandardCharsets.UTF_8);
        Mapper.readMappings(vanilla, forge);

        Logger.warn("Loading Hooks...");
        Transformer transformer = new Transformer(JVMTIWrapper.instance::getClassBytes);

        byte[] classFindHook = ASMUtils.rewriteClass(Objects.requireNonNull(ClassMapper.map(ASMUtils.node(getClassFindHook()))));
        ClassNode classFindHookNode = ASMUtils.node(classFindHook);
        Class<?> LaunchClassLoaderClass = Objects.requireNonNull(Mixin.Helper.getAnnotation(classFindHookNode)).value();

        boolean hasLaunchClassLoader = LaunchClassLoaderClass != null;
        if (hasLaunchClassLoader)
            transformer.addMixin(classFindHookNode);

        byte[] initHook = ASMUtils.rewriteClass(Objects.requireNonNull(ClassMapper.map(ASMUtils.node(getInitHook()))));
        ClassNode initHookNode = ASMUtils.node(initHook);
        Class<?> MinecraftClass = Objects.requireNonNull(Mixin.Helper.getAnnotation(initHookNode)).value();
        transformer.addMixin(initHookNode);

        val map = transformer.transform();
        if (hasLaunchClassLoader)
            Logger.info("Redefined {} ReturnCode: {}", LaunchClassLoaderClass, JVMTIWrapper.instance.redefineClass(LaunchClassLoaderClass, map.get(LaunchClassLoaderClass.getName())));
        Logger.info("Redefined {} ReturnCode: {}", MinecraftClass, JVMTIWrapper.instance.redefineClass(MinecraftClass, map.get(MinecraftClass.getName())));
    }
}
