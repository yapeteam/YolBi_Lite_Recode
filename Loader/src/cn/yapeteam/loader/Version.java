package cn.yapeteam.loader;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Version {
    V1_8_9("1.8.9"), V1_12_2("1.12.2");

    private final String version;

    Version(String version) {
        this.version = version;
    }

    public static Version get() {
        Version version = Arrays.stream(values()).filter(v -> System.getProperty("java.library.path").contains(v.version)).findFirst().orElse(null);
        if (version != null) return version;
        return Arrays.stream(values()).filter(v -> System.getProperty("sun.java.command").contains(" " + v.version.substring(0, 3) + " ")).findFirst().orElse(null);
    }
}
