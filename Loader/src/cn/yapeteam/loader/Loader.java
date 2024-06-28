package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.ymixin.utils.Mapper;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Loader {
    public static final String YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi").getAbsolutePath();
    public static final int port = 20181;

    public static void preload() {
        Logger.init();
        try {
            Logger.info("Start PreLoading...");
            Logger.warn("ClassLoader: " + BootStrap.client_thread.getContextClassLoader().getClass().getName());
            SocketSender.init();
            try {
                UIManager.getDefaults().put("ClassLoader", ClassLoader.getSystemClassLoader());
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            } catch (UnsupportedLookAndFeelException e) {
                Logger.exception(e);
            }
            Logger.warn("Start Mapping Injection!");
            JarMapper.dispose(new File(YOLBI_DIR, "injection/injection.jar"), "injection.jar");
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
