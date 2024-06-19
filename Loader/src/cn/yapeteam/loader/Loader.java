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
    public static final int port = 20181;

    public static void preload() {
        Logger.init();
        try {
            Logger.info("Start PreLoading...");
            SocketSender.init();
            try {
                UIManager.getDefaults().put("ClassLoader", BootStrap.client_thread.getContextClassLoader());
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
