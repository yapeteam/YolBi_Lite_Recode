package cn.yapeteam.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Terminal {
    private final File dir;
    private final String[] envp;
    private boolean reading = false;

    public Terminal(File dir, String[] envp) {
        this.dir = dir;
        this.envp = envp;
    }

    public void execute(String[] command) throws Exception {
        System.out.printf("Running command: %s\n", String.join(" ", command));
        Process proc = Runtime.getRuntime().exec(command, envp, dir);
        reading = true;
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), "gbk"));
                String line;
                while (reading) {
                    line = reader.readLine();
                    if (line != null) System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream(), "gbk"));
                String line;
                while (reading) {
                    line = reader.readLine();
                    if (line != null) {
                        if (line.contains("error:"))
                            System.err.println(line);
                        else if (line.contains("warning:"))
                            System.out.printf("\033[33m%s\033[0m\n", line);
                        else if (line.contains("note:"))
                            System.out.printf("\033[0;36m%s\033[0m\n", line);
                        else
                            System.out.println(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        proc.waitFor();
        reading = false;
    }
}
