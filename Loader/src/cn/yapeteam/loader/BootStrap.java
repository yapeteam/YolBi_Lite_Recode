package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.loader.utils.Pair;
import cn.yapeteam.ymixin.YMixin;
import cn.yapeteam.ymixin.utils.ASMUtils;
import cn.yapeteam.ymixin.utils.Mapper;
import lombok.Getter;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    public static Thread client_thread = null;

    private static Pair<Version, Mapper.Mode> getMinecraftVersion() {
        Mapper.Mode mode;
        Version version = Version.get();
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
    @Getter
    private static Pair<Version, Mapper.Mode> version;

    private static ClassNode getMinecraftClassNode() {
        return ASMUtils.node(JVMTIWrapper.instance.getClassBytes(ClassUtils.getClass(Mapper.getObfClass("net.minecraft.client.Minecraft"))));
    }

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
            version = getMinecraftVersion();
            if (version == null || version.first == null || version.second == null) {
                Logger.error("Failed to get Minecraft version.");
                if (version != null) {
                    Logger.error("Version: {}, Mode: {}", version.first, version.second);
                    Logger.error("java.library.path: {}, sun.java.command: {}", System.getProperty("java.library.path"), System.getProperty("sun.java.command"));
                } else Logger.error("Version: null, Mode: null");
                SocketSender.send("CLOSE");
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
            Mapper.readMapping(new String(Objects.requireNonNull(ResourceManager.resources.get("mappings/" + version.first.getVersion() + "/vanilla.srg")), StandardCharsets.UTF_8), Mapper.getVanilla());
            Mapper.readMapping(new String(Objects.requireNonNull(ResourceManager.resources.get("mappings/" + version.first.getVersion() + "/forge.srg")), StandardCharsets.UTF_8), Mapper.getSearges());

            Logger.warn("Loading Hooks...");

            ClassNode target;
            try {
                target = getMinecraftClassNode();
            } catch (Exception e) {
                Thread.sleep(500);
                target = getMinecraftClassNode();
            }
            if (target == null) {
                Logger.error("Failed to get Minecraft class node.");
                SocketSender.send("CLOSE");
                return;
            }
            String targetMethod = Mapper.map("net/minecraft/client/Minecraft", "runGameLoop", "()V", Mapper.Type.Method);
            for (MethodNode method : target.methods) {
                if (method.name.equals(targetMethod) && method.desc.equals("()V")) {
                    LabelNode labelNode = new LabelNode();
                    InsnList insnList = new InsnList();
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(BootStrap.class), "initHook", "()V"));
                    insnList.add(labelNode);
                    method.instructions.insert(insnList);
                    break;
                }
            }
            Class<?> MinecraftClass = ClassUtils.getClass(Mapper.getObfClass("net.minecraft.client.Minecraft"));

            Logger.info("Redefined {} ReturnCode: {}", MinecraftClass, JVMTIWrapper.instance.redefineClass(MinecraftClass, ASMUtils.rewriteClass(target)));
        } catch (Throwable e) {
            Logger.exception(e);
        }
    }
}
