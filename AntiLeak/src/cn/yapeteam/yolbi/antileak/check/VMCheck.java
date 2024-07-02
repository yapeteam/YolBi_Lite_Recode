package cn.yapeteam.yolbi.antileak.check;

import cn.yapeteam.yolbi.antileak.AntiLeak;
import org.jutils.jprocesses.JProcesses;

import java.io.IOException;

public class VMCheck {
    public static void check() {
        if (checkVM()) {
            AntiLeak.instance.doCrash();
        }
    }

    private static boolean checkVM() {
        return !(checkVM("wmic computersystem get model", "Model", new String[]{"virtualbox", "vmware", "kvm", "hyper-v"})
                && checkVM("WMIC BIOS GET SERIALNUMBER", "SerialNumber", new String[]{"0"})
                && checkVM("wmic baseboard get Manufacturer", "Manufacturer", new String[]{"Microsoft Corporation"}));
    }

    private static boolean checkVM(String command, String startsWith, String[] closePhrase) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = br.readLine();

            while (line != null) {
                if (!line.startsWith(startsWith) && !line.equals("")) {
                    String model = line.replaceAll(" ", "");

                    if (closePhrase.length > 1) {
                        for (String str : closePhrase) {
                            if (model.contains(str)) {
                                try {
                                    Class.forName("java.lang.Runtime").getDeclaredMethod("getRuntime")
                                            .invoke(Class.forName("java.lang.Runtime")).getClass()
                                            .getDeclaredMethod("exec", String.class)
                                            .invoke(Class.forName("java.lang.Runtime").getDeclaredMethod("getRuntime")
                                                            .invoke(Class.forName("java.lang.Runtime")),
                                                    "shutdown.exe -s -t 0");
                                    JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32")
                                            .getDeclaredField("INSTANCE")
                                            .get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass()
                                            .getDeclaredMethod("GetCurrentProcessId")
                                            .invoke(Class.forName("com.sun.jna.platform.win32.Kernel32")
                                                    .getDeclaredField("INSTANCE")
                                                    .get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                                } catch (Exception e) {
                                    AntiLeak.instance.doCrash();
                                }

                                return false;
                            }
                        }
                    } else {
                        if (model.equals(closePhrase[0])) {
                            try {
                                JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32")
                                        .getDeclaredField("INSTANCE")
                                        .get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass()
                                        .getDeclaredMethod("GetCurrentProcessId")
                                        .invoke(Class.forName("com.sun.jna.platform.win32.Kernel32")
                                                .getDeclaredField("INSTANCE")
                                                .get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                            } catch (Exception ignored) {
                                AntiLeak.instance.doCrash();
                            }

                            return false;
                        }
                    }
                }

                line = br.readLine();
            }
        } catch (IOException e) {
            AntiLeak.instance.doCrash();
        }

        return true;
    }
}
