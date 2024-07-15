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

    private static Thread getThreadByName(String name) {
        for (Object o : Thread.getAllStackTraces().keySet().toArray()) {
            Thread thread = (Thread) o;
            if (thread.getName().equals(name))
                return thread;
        }
        return null;
    }

    public static void entry() {
        try {
            if (JVMTIWrapper.instance == null)
                JVMTIWrapper.instance = new NativeWrapper();
            System.setProperty("java.awt.headless", "true");
            client_thread = getThreadByName("Client thread");
            if (client_thread == null)
                client_thread = getThreadByName("Render thread");
            if (client_thread == null) {
                Logger.error("Failed to get target thread.");
                SocketSender.send("CLOSE");
                return;
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
                        } catch (Throwable e) {
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
                Logger.error("Unsupported Minecraft version.");
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

            Logger.warn("Loading Minecraft Hook...");

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
            String methodName = "runTick";
            String desc = version.first != Version.V1_18_1 ? "()V" : "(Z)V";
            String targetMethod = Mapper.map("net/minecraft/client/Minecraft", methodName, desc, Mapper.Type.Method);
            Logger.info("Mapped target method: {} {}", targetMethod, desc);
            for (MethodNode method : target.methods) {
                if (method.name.equals(targetMethod) && method.desc.equals(desc)) {
                    Logger.info("Found target method: {} {}", method.name, method.desc);
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
