//package cn.yapeteam.injector;
//
//import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
//
//import javax.swing.*;
//        import java.io.File;
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.URL;
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
//
//        StartFrame startFrame = new StartFrame();
//        startFrame.setVisible(true);
//
//        if (checkConnection()) {
//            startFrame.dispose();
//            MainFrame frame = new MainFrame();
//            new Thread(() -> frame.setVisible(true)).start();
//            if (args.length == 1) {
//                frame.inject_dll(Integer.parseInt(args[0]));
//                frame.inject_ui();
//            }
//        } else {
//            startFrame.dispose();
//            JOptionPane.showMessageDialog(null, "Failed to connect to the Yolbi Server", "Connection Error", JOptionPane.ERROR_MESSAGE);
//            System.exit(0);
//        }
//    }
//
//    private static boolean checkConnection() {
//        try {
//
//            URL url = new URL("http://yapeteam.github.io");
//            URL url2 = new URL("http://skidonion.tech");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
//            connection.setRequestMethod("HEAD");
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//
//            connection2.setRequestMethod("HEAD");
//            connection2.setConnectTimeout(5000);
//            connection2.setReadTimeout(5000);
//            int responseCode = connection.getResponseCode();
//            int responseCode2 = connection2.getResponseCode();
//            return (200 <= responseCode && responseCode <= 399 && responseCode2 >= 200 && responseCode2 <= 399);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}
//

package cn.yapeteam.injector;
import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final String version = "0.3.6";
    public static final File YolBi_Dir = new File(System.getProperty("user.home"), ".yolbi");
    public static final String dllName = "libinjection.dll";
    public static final int port = 20181;
    public static final String HWID_URL = "https://gitee.com/wzhy233/yolbi-lite/raw/master/hwidlist.json"; // hwidlist.json
    public static final String HWID = getHWID();

    public static void main(String[] args) throws Exception {
        Utils.unzip(Main.class.getResourceAsStream("/injection.zip"), YolBi_Dir);
        System.load(new File(Main.YolBi_Dir, "libapi.dll").getAbsolutePath());
        UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());

        StartFrame startFrame = new StartFrame();
        startFrame.setVisible(true);

        if (checkConnection()) {
            if (!downloadHWIDList()) {
                startFrame.dispose();
                JOptionPane.showMessageDialog(null, "Failed to connect to the Yolbi Server", "Connection Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

            if (!isHWIDValid()) {
                startFrame.dispose();
                copyHWIDToClipboard(HWID); // ����HWID��������
                JOptionPane.showMessageDialog(null, "You do not have permission to access the Yolbi Lite, please add this code administrator: " + HWID, "Validation failed , Your HWID has benn copied to The Clipboard", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

            startFrame.dispose();
            MainFrame frame = new MainFrame();
            new Thread(() -> frame.setVisible(true)).start();
            if (args.length == 1) {
                frame.inject_dll(Integer.parseInt(args[0]));
                frame.inject_ui();
            }
        } else {
            startFrame.dispose();
            JOptionPane.showMessageDialog(null, "Failed to connect to the Yolbi Server", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private static boolean checkConnection() {
        try {
            URL url = new URL("http://yapeteam.github.io");//单纯检查连接,无验证意义
            URL url2 = new URL("http://skidonion.tech");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection2.setRequestMethod("HEAD");
            connection2.setConnectTimeout(5000);
            connection2.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            int responseCode2 = connection2.getResponseCode();
            return (200 <= responseCode && responseCode <= 399 && responseCode2 >= 200 && responseCode2 <= 399);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean downloadHWIDList() {
        try {
            URL url = new URL(HWID_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            InputStream inputStream = connection.getInputStream();
            File hwidDir = new File(YolBi_Dir, "verify");
            if (!hwidDir.exists()) {
                hwidDir.mkdirs();
            }
            File hwidFile = new File(hwidDir, "hwidlist.json");

            try (FileOutputStream outputStream = new FileOutputStream(hwidFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isHWIDValid() {
        try {
            File hwidFile = new File(YolBi_Dir, "verify/hwidlist.json");
            if (!hwidFile.exists()) {
                return false;
            }
            String content = new String(Files.readAllBytes(Paths.get(hwidFile.getAbsolutePath())), StandardCharsets.UTF_8);
            List<String> hwidList = parseHWIDList(content);
            String currentHWID = getHWID();

            for (String hwid : hwidList) {
                if (hwid.equals(currentHWID)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static List<String> parseHWIDList(String content) {
        List<String> hwidList = new ArrayList<>();
        content = content.replace("[", "").replace("]", "").replace("\"", "").trim();
        String[] hwids = content.split(",");
        for (String hwid : hwids) {
            hwidList.add(hwid.trim());
        }
        return hwidList;
    }

    private static String getHWID() {
        try {
            StringBuilder hwid = new StringBuilder();
            hwid.append(getCPUID());
            hwid.append(getMotherboardSerial());
            hwid.append(getDiskSerial());

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(hwid.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getCPUID() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});
        process.getOutputStream().close();
        Scanner sc = new Scanner(process.getInputStream());
        sc.next(); // Skip "ProcessorId" line
        return sc.next().trim();
    }

    private static String getMotherboardSerial() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"wmic", "baseboard", "get", "SerialNumber"});
        process.getOutputStream().close();
        Scanner sc = new Scanner(process.getInputStream());
        sc.next(); // Skip "SerialNumber" line
        return sc.next().trim();
    }

    private static String getDiskSerial() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"wmic", "diskdrive", "get", "SerialNumber"});
        process.getOutputStream().close();
        Scanner sc = new Scanner(process.getInputStream());
        sc.next(); // Skip "SerialNumber" line
        return sc.next().trim();
    }

    private static void copyHWIDToClipboard(String hwid) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection stringSelection = new StringSelection(hwid);
        clipboard.setContents(stringSelection, null);
    }
}
