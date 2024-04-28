package cn.yapeteam.injector;

import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;

import javax.swing.*;
import java.io.File;

public class Main {
    public static final File YolBi_Dir = new File(System.getProperty("user.home"), ".yolbi");
    public static final String dllName = "injection.dll", agentName = "agent.jar";
    public static final int port = 20181;

    public static void main(String[] args) throws Exception {
        Utils.unzip(Main.class.getResourceAsStream("/injection.zip"), YolBi_Dir);
        UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());
        MainFrame frame = new MainFrame();
        new Thread(() -> frame.setVisible(true)).start();
        if (args.length == 2) {
            switch (args[0]) {
                case "agent":
                    frame.inject_agent(Integer.parseInt(args[1]));
                    break;
                case "dll":
                    frame.inject_dll(Integer.parseInt(args[1]));
            }
            frame.inject_ui();
        }
    }
}
