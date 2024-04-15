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
        new MainFrame().setVisible(true);
    }
}
