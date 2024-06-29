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

    public static Version parse(String libraryPath) {
        if (libraryPath == null) return null;
        return Arrays.stream(values()).filter(v -> libraryPath.contains(v.version)).findFirst().orElse(null);
    }
}
