package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.ymixin.utils.ASMUtils;
import lombok.Getter;
import lombok.val;
import org.objectweb.asm_9_2.tree.AbstractInsnNode;
import org.objectweb.asm_9_2.tree.ClassNode;
import org.objectweb.asm_9_2.tree.LdcInsnNode;
import org.objectweb.asm_9_2.tree.MethodNode;

import java.util.Arrays;
import java.util.regex.Pattern;

@Getter
public enum Version {
    V1_8_9("1.8.9", "net/minecraft/client/multiplayer/ServerData", "bde"),
    V1_12_2("1.12.2", "net/minecraft/client/multiplayer/ServerData", "bse"),
    V1_18_1("1.18.1");

    private final String version;
    private final String[] serverDataClasses;

    Version(String version, String... obfServerData) {
        this.version = version;
        this.serverDataClasses = obfServerData;
    }

    private static final Pattern pattern = Pattern.compile("^[0-9.]+$");

    private static Version getVersionByServerData(String clzName) {
        Class<?> ServerDataClass = ClassUtils.getClass(clzName);
        if (ServerDataClass == null)
            return null;
        ClassNode ServerDataClassNode = null;
        while (ServerDataClassNode == null) {
            try {
                ServerDataClassNode = ASMUtils.node(JVMTIWrapper.instance.getClassBytes(ServerDataClass));
                Thread.sleep(500);
            } catch (Exception ignored) {
            }
        }
        MethodNode methodNode = ServerDataClassNode.methods.stream().filter(m -> m.name.equals("<init>")).findFirst().orElse(null);
        if (methodNode == null)
            return null;
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (instruction instanceof LdcInsnNode) {
                String versionString = ((LdcInsnNode) instruction).cst.toString();
                if (pattern.matcher(versionString).matches()) {
                    Logger.info("Found version: \"{}\"", versionString);
                    return Arrays.stream(values()).filter(v -> v.version.equals(versionString)).findFirst().orElse(null);
                } else break;// <init>中只有一个LDC指令，且指令值是版本号，因此可以直接返回
            }
        }
        return null;
    }

    public static Version get() {
        for (Version value : values()) {
            if (System.getProperty("java.library.path").contains(value.version)) return value;
            if (System.getProperty("sun.java.command").contains(" " + value.version.substring(0, value.version.lastIndexOf("."))))
                return value;
            for (String clz : value.serverDataClasses) {
                val versionByServerData = getVersionByServerData(clz);
                if (versionByServerData != null) return versionByServerData;
            }
        }
        return null;
    }
}
