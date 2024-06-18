package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.ymixin.YMixin;
import cn.yapeteam.ymixin.utils.ASMUtils;
import cn.yapeteam.ymixin.utils.Mapper;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.tree.ClassNode;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@SuppressWarnings("unused")
public class Loader {
    public static final int ASM_API = Opcodes.ASM9;
    public static final String YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi").getAbsolutePath();
    public static Thread client_thread = null;
    public static final int port = 20181;

    public static Mapper.Mode guessMappingMode() {
        Class<?> clazz = ClassUtils.getClass("net.minecraft.client.Minecraft");
        if (clazz == null) return Mapper.Mode.Vanilla;
        byte[] bytes = JVMTIWrapper.instance.getClassBytes(clazz);
        ClassNode node = ASMUtils.node(bytes);
        if (node.methods.stream().anyMatch(m -> m.name.equals("runTick")))
            return Mapper.Mode.None;
        return Mapper.Mode.Searge;
    }

    public static void preload() {
        Logger.init();
        try {
            if (JVMTIWrapper.instance == null)
                JVMTIWrapper.instance = new NativeWrapper();
            Logger.info("Start PreLoading...");
            Mapper.Mode mode = guessMappingMode();
            YMixin.init(ClassUtils::getClass, new cn.yapeteam.ymixin.Logger() {
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
            });
            Logger.info("Reading mappings, mode: {}", mode.name());
            Mapper.setMode(mode);
            String vanilla = new String(Objects.requireNonNull(ResourceManager.resources.get("mappings/vanilla.srg")), StandardCharsets.UTF_8);
            String forge = new String(Objects.requireNonNull(ResourceManager.resources.get("mappings/forge.srg")), StandardCharsets.UTF_8);
            Mapper.readMappings(vanilla, forge);
            SocketSender.init();
            try {
                for (Object o : Thread.getAllStackTraces().keySet().toArray()) {
                    Thread thread = (Thread) o;
                    if (thread.getName().equals("Client thread")) {
                        client_thread = thread;
                        UIManager.getDefaults().put("ClassLoader", thread.getContextClassLoader());
                        break;
                    }
                }
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            } catch (UnsupportedLookAndFeelException e) {
                Logger.exception(e);
            }
            Logger.warn("Start Mapping Injection!");
            JarMapper.dispose(new File(YOLBI_DIR, "injection/injection.jar"), new File(YOLBI_DIR, "injection.jar"));
            Logger.success("Completed");
            Mapper.getCache().clear();
        } catch (Throwable e) {
            Logger.exception(e);
            try {
                Logger.writeCache();
                Desktop.getDesktop().open(Logger.getLog());
            } catch (Throwable ignored) {
            }
        }
    }
}
