package cn.yapeteam.yolbi.antileak.check;

import cn.yapeteam.yolbi.antileak.AntiLeak;
import com.sun.tools.attach.VirtualMachine;

import java.util.Arrays;

public class JvmProcessCheck {
    private static final String[] badProcess = {
            "dump", "dumper", "packetlog", "logger", "recaf", "jbyte", "bytecode", "decompile", "log"
    };

    public static void check() {
        if (hasBadProcess()) {
            AntiLeak.instance.crash();
        }
    }

    private static boolean hasBadProcess() {
        return VirtualMachine.list().stream().anyMatch(descriptor -> {
            final String name = descriptor.displayName().toLowerCase().trim();

            return Arrays.stream(badProcess).anyMatch(name::contains);
        });
    }
}
