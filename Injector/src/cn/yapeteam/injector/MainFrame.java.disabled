package cn.yapeteam.injector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import cn.yapeteam.injector.Main;

public class MainFrame extends JFrame {
    private JPanel panel;
    private JButton inject;
    private JComboBox<String> process;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;

    private volatile float value1 = 0, value2 = 0;

    private ArrayList<Pair<String, Integer>> targets = new ArrayList<>();
    private final Thread progressThread1;
    private final Thread progressThread2;
    private Thread serverThread;
    private final Thread updateThread;

    public MainFrame() {
        super("YolBi Lite v" + Main.version + " - Development Build");
        float width = 500, height = width * 0.618f;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        int[] size = {(int) (width / 1920 * screenWidth + 225 ), (int) (height / 1080 * screenHeight + 100)};
        setSize(size[0], size[1]);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        add(panel);
        progressBar1.setVisible(false);
        progressBar2.setVisible(false);

        getRootPane().setDefaultButton(inject);
        inject.addActionListener(e -> {
            if (!targets.isEmpty() && process.getSelectedIndex() != -1)
                inject(targets.get(process.getSelectedIndex()).b);
        });
        float speed = 0.1f;
        int fps = 60;
        progressThread1 = new Thread(() -> {
            float cache = 0;
            while (true) {
                cache += (value1 - cache) * speed;
                progressBar1.setValue((int) cache);
                try {
                    Thread.sleep(1000 / fps);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        });
        progressThread2 = new Thread(() -> {
            float cache = 0;
            while (true) {
                cache += (value2 - cache) * speed;
                progressBar2.setValue((int) cache);
                try {
                    Thread.sleep((long) (1000 / fps));
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        });
        serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(Main.port)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> {
                        try {
                            InputStream stream = socket.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                            while (true) {
                                String message = reader.readLine();
                                String[] values = message.split(" ");
                                if (values.length == 2) {
                                    float value = Float.parseFloat(values[1]);
                                    switch (values[0]) {
                                        case "P1":
                                            value1 = value;
                                            break;
                                        case "P2":
                                            value2 = value;
                                            break;
                                    }
                                } else switch (message) {
                                    case "S1":
                                        progressThread1.start();
                                        progressBar1.setVisible(true);
                                        break;
                                    case "S2":
                                        progressThread2.start();
                                        progressBar2.setVisible(true);
                                        break;
                                    case "E1":
                                        progressThread1.interrupt();
                                        progressBar1.setVisible(false);
                                        break;
                                    case "E2":
                                        progressThread2.interrupt();
                                        progressBar2.setVisible(false);
                                        break;
                                    case "CLOSE":
                                        try {
                                            Class.forName("Start");
                                            socket.close();
                                            setVisible(false);
                                        } catch (ClassNotFoundException e) {
                                            progressThread1.interrupt();
                                            progressThread2.interrupt();
                                            serverThread.interrupt();
                                            System.exit(0);
                                        }
                                }
                            }
                        } catch (IOException ignored) {
                        }
                    }).start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        updateThread = new Thread(() -> {
            while (true) {
                ArrayList<Pair<String, Integer>> minecraftProcesses = Utils.getMinecraftProcesses();
                int selected = process.getSelectedIndex();
                process.removeAllItems();
                if (minecraftProcesses.isEmpty()) continue;
                for (Pair<String, Integer> minecraftProcess : minecraftProcesses)
                    process.addItem(minecraftProcess.a);
                if (selected != -1)
                    process.setSelectedIndex(selected);
                targets = minecraftProcesses;
                long time = System.currentTimeMillis();
                while (true) if (System.currentTimeMillis() - time >= 500) break;
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) updateThread.start();
    }

    public void inject(int pid) {
        System.out.println(pid);
        inject_dll(pid);
        inject_ui();
    }

    public void inject_dll(int pid) {
        new Thread(() -> Utils.injectDLL(pid, new File(Main.YolBi_Dir, Main.dllName).getAbsolutePath())).start();
    }

    public void inject_ui() {
        updateThread.interrupt();
        serverThread.start();
        process.setVisible(false);
        inject.setVisible(false);
    }
}