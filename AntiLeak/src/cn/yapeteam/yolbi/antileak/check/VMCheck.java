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

    // 检查系统是否为虚拟机
    private static boolean checkVM() {
        // 使用命令行工具wmic查询系统型号、序列号和制造商
        return !(checkVM("wmic computersystem get model", "Model", new String[]{"virtualbox", "vmware", "kvm", "hyper-v"})
                && checkVM("WMIC BIOS GET SERIALNUMBER", "SerialNumber", new String[]{"0"})
                && checkVM("wmic baseboard get Manufacturer", "Manufacturer", new String[]{"Microsoft Corporation"}));
    }

    // 检查虚拟机
private static boolean checkVM(String command, String startsWith, String[] closePhrase) {
        try {
            // 执行命令
            Process p = Runtime.getRuntime().exec(command);
            // 读取命令的输出
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = br.readLine();

            while (line != null) {
                // 判断输出是否以startsWith开头
                if (!line.startsWith(startsWith) && !line.equals("")) {
                    // 获取输出中的model
                    String model = line.replaceAll(" ", "");

                    if (closePhrase.length > 1) {
                        // 如果closePhrase长度大于1，遍历closePhrase
                        for (String str : closePhrase) {
                            // 如果model中包含closePhrase
                            if (model.contains(str)) {
                                try {
                                    // 关闭虚拟机
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
                                    // 如果关闭虚拟机失败，则调用doCrash方法
                                    AntiLeak.instance.doCrash();
                                }

                                return false;
                            }
                        }
                    } else {
                        // 如果closePhrase长度等于1，判断model是否等于closePhrase
                        if (model.equals(closePhrase[0])) {
                            try {
                                // 关闭虚拟机
                                JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32")
                                        .getDeclaredField("INSTANCE")
                                        .get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass()
                                        .getDeclaredMethod("GetCurrentProcessId")
                                        .invoke(Class.forName("com.sun.jna.platform.win32.Kernel32")
                                                .getDeclaredField("INSTANCE")
                                                .get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                            } catch (Exception ignored) {
                                // 如果关闭虚拟机失败，则调用doCrash方法
                                AntiLeak.instance.doCrash();
                            }

                            return false;
                        }
                    }
                }

                line = br.readLine();
            }
        } catch (IOException e) {
            // 如果读取命令的输出失败，则调用doCrash方法
            AntiLeak.instance.doCrash();
        }

        return true;
    }
}
