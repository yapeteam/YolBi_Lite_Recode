package cn.yapeteam.injector;

import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;

import javax.swing.*;
import java.io.File;

public class Main {
    public static final File YolBi_Dir = new File(System.getProperty("user.home"), ".yolbi");
    public static final String dllName = "libinjection.dll";
    public static final int port = 20181;

    public static void main(String[] args) throws Exception {
        Utils.unzip(Main.class.getResourceAsStream("/injection.zip"), YolBi_Dir);
        System.load(new File(Main.YolBi_Dir, "libapi.dll").getAbsolutePath());
        UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());
        MainFrame frame = new MainFrame();
        new Thread(() -> frame.setVisible(true)).start();
        if (args.length == 1) {
            frame.inject_dll(Integer.parseInt(args[0]));
            frame.inject_ui();
        }
    }
}
