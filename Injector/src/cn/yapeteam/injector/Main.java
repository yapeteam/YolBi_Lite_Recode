//package cn.yapeteam.injector;
//
//import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
//
//import javax.swing.*;
//import java.io.File;
//
//public class Main {
//    public static final String version = "0.3.6";
//    public static final File YolBi_Dir = new File(System.getProperty("user.home"), ".yolbi");
//    public static final String dllName = "libinjection.dll";
//    public static final int port = 20181;
//
//    public static void main(String[] args) throws Exception {
//        Utils.unzip(Main.class.getResourceAsStream("/injection.zip"), YolBi_Dir);
//        System.load(new File(Main.YolBi_Dir, "libapi.dll").getAbsolutePath());
//        UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());
//        MainFrame frame = new MainFrame();
//        new Thread(() -> frame.setVisible(true)).start();
//        if (args.length == 1) {
//            frame.inject_dll(Integer.parseInt(args[0]));
//            frame.inject_ui();
//        }
//    }
//}
package cn.yapeteam.injector;

import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static final String version = "0.3.6";
    public static final File YolBi_Dir = new File(System.getProperty("user.home"), ".yolbi");
    public static final String dllName = "libinjection.dll";
    public static final int port = 20181;

    public static void main(String[] args) throws Exception {
        Utils.unzip(Main.class.getResourceAsStream("/injection.zip"), YolBi_Dir);
        System.load(new File(Main.YolBi_Dir, "libapi.dll").getAbsolutePath());
        UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());

        StartFrame startFrame = new StartFrame();
        startFrame.setVisible(true);

        if (checkConnection()) {
            startFrame.dispose();
            MainFrame frame = new MainFrame();
            new Thread(() -> frame.setVisible(true)).start();
            if (args.length == 1) {
                frame.inject_dll(Integer.parseInt(args[0]));
                frame.inject_ui();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Failed to connect to the Yolbi Server", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private static boolean checkConnection() {
        try {
            URL url = new URL("http://yapeteam.github.io");
            URL url2 = new URL("http://www.bilibili.com");
            URL url3 = new URL("http://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
            HttpURLConnection connection3 = (HttpURLConnection) url3.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection2.setRequestMethod("HEAD");
            connection2.setConnectTimeout(5000);
            connection2.setReadTimeout(5000);

            connection3.setRequestMethod("HEAD");
            connection3.setConnectTimeout(5000);
            connection3.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            int responseCode2 = connection2.getResponseCode();
            int responseCode3 = connection3.getResponseCode();
            return (200 <= responseCode && responseCode <= 399 && responseCode2 <= 399 && responseCode2 >=200);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
