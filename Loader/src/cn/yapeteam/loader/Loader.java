package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.objectweb.asm_9_2.Opcodes;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@SuppressWarnings("unused")
public class Loader {
    public static final int ASM_API = Opcodes.ASM9;
    public static final String YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi").getAbsolutePath();
    public static Thread client_thread = null;
    public static final int port = 20181;

    public static void preload() {
        Logger.init();
        try {
            if (JVMTIWrapper.instance == null)
                JVMTIWrapper.instance = new NativeWrapper();
            Logger.info("Start PreLoading...");
            JOptionPane.showMessageDialog(null, "1", "Warning", JOptionPane.WARNING_MESSAGE);
            Mapper.Mode mode = Mapper.guessMappingMode();
            JOptionPane.showMessageDialog(null, "2", "Warning", JOptionPane.WARNING_MESSAGE);
            Logger.info("Reading mappings, mode: {}", mode.name());
            JOptionPane.showMessageDialog(null, "3", "Warning", JOptionPane.WARNING_MESSAGE);
            Mapper.setMode(mode);
            JOptionPane.showMessageDialog(null, "4", "Warning", JOptionPane.WARNING_MESSAGE);
            Mapper.readMappings();
            JOptionPane.showMessageDialog(null, "5", "Warning", JOptionPane.WARNING_MESSAGE);
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
