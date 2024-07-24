package cn.yapeteam.injector;

import javax.swing.*;
import java.awt.*;
import cn.yapeteam.injector.Main;
import java.io.File;

public class StartFrame extends JFrame {
    private JPanel panel;

    public StartFrame() {
        setTitle("YolBi Shield");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        File shieldImageFile = new File(Main.YolBi_Dir, "resources/shield.png");
        if (shieldImageFile.exists()) {
            ImageIcon shieldIcon = new ImageIcon(shieldImageFile.getAbsolutePath());
            JLabel shieldLabel = new JLabel(shieldIcon);
            panel.add(shieldLabel, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("Sorry , but we are still working.");
            panel.add(errorLabel, BorderLayout.CENTER);
        }

        add(panel);
    }
}
