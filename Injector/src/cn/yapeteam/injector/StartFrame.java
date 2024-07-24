package cn.yapeteam.injector;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class StartFrame extends JFrame {
    private JPanel panel;

    public StartFrame() {
        setTitle("YolBi Shield");
        setSize(600, 600);
        setAlwaysOnTop(true);
        setUndecorated(true); // Remove window decorations
        setBackground(new Color(0, 0, 0, 0)); // Set frame background to transparent
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false); // Make panel background transparent

        File shieldImageFile = new File(Main.YolBi_Dir, "resources/image/yolbi-shield.png");
        if (shieldImageFile.exists()) {
            ImageIcon shieldIcon = new ImageIcon(shieldImageFile.getAbsolutePath());
            JLabel shieldLabel = new JLabel(shieldIcon);
            panel.add(shieldLabel, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("Sorry ,but we are still working");
            panel.add(errorLabel, BorderLayout.CENTER);
        }

        add(panel);
    }
}
