package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ASMUtils;
import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.loader.utils.Pair;
import cn.yapeteam.ymixin.Transformer;
import cn.yapeteam.ymixin.YMixin;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.utils.Mapper;
import lombok.val;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * native used
 * native invoked
 */
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

    public static Thread client_thread = null;

    private static Pair<Version, Mapper.Mode> getMinecraftVersion() {
        Mapper.Mode mode;
        Version version = Version.parse(System.getProperty("java.library.path"));
        if (version == null)
            version = Version.parse(System.getProperty("sun.java.command"));
        Class<?> clazz = ClassUtils.getClass(("net.minecraft.client.Minecraft"));
        if (clazz != null) {
            byte[] bytes = JVMTIWrapper.instance.getClassBytes(clazz);
            if (bytes != null) {
                ClassNode node = ASMUtils.node(bytes);
                if (node.methods.stream().anyMatch(method -> method.name.equals("runTick")))
                    mode = Mapper.Mode.None;
                else mode = Mapper.Mode.Searge;
            } else return null;
        } else mode = Mapper.Mode.Vanilla;
        return new Pair<>(version, mode);
    }

    public static boolean hasLaunchClassLoader = true;

    public static void entry() {
        try {
            if (JVMTIWrapper.instance == null)
                JVMTIWrapper.instance = new NativeWrapper();
            for (Object o : Thread.getAllStackTraces().keySet().toArray()) {
                Thread thread = (Thread) o;
                if (thread.getName().equals("Client thread")) {
                    client_thread = thread;
                    break;
                }
            }
            try {
                Class.forName("net.minecraft.launchwrapper.LaunchClassLoader", true, client_thread.getContextClassLoader());
            } catch (ClassNotFoundException e) {
                hasLaunchClassLoader = false;
            }
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
            Pair<Version, Mapper.Mode> version = getMinecraftVersion();
            if (version == null || version.first == null || version.second == null) {
                Logger.error("Failed to get Minecraft version, please check your game version.");
                return;
            }
            String branch = "null";
            switch (version.second) {
                case Vanilla:
                    branch = "vanilla";
                    break;
                case Searge:
                    branch = "forge";
                    break;
                case None:
                    branch = "mcp";
            }
            Logger.info("Minecraft version: {} ({})", version.first.getVersion(), branch);
            Mapper.Mode mode = version.second;
            Logger.info("Reading mappings, mode: {}", mode.name());
            Mapper.setMode(mode);
            String vanilla = new String(Objects.requireNonNull(ResourceManager.resources.get("mappings/" + version.first.getVersion() + "/vanilla.srg")), StandardCharsets.UTF_8);
            String forge = new String(Objects.requireNonNull(ResourceManager.resources.get("mappings/" + version.first.getVersion() + "/forge.srg")), StandardCharsets.UTF_8);
            Mapper.readMappings(vanilla, forge);
            if (version.first != Version.V1_8_9) {
                Logger.error("Unsupported Minecraft version: 1.12.2");
                return;
            }

            Logger.warn("Loading Hooks...");
            Transformer transformer = new Transformer(JVMTIWrapper.instance::getClassBytes);

            byte[] initHook = ASMUtils.rewriteClass(Objects.requireNonNull(ClassMapper.map(ASMUtils.node(getInitHook()))));
            ClassNode initHookNode = ASMUtils.node(initHook);
            Class<?> MinecraftClass = Objects.requireNonNull(Mixin.Helper.getAnnotation(initHookNode)).value();
            transformer.addMixin(initHookNode);

            val map = transformer.transform();
            Logger.info("Redefined {} ReturnCode: {}", MinecraftClass, JVMTIWrapper.instance.redefineClass(MinecraftClass, map.get(MinecraftClass.getName())));
        } catch (Throwable e) {
            Logger.exception(e);
        }
    }
}
