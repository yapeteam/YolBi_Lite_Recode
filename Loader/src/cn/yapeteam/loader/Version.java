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

    public static Version parse(String version) {
        return Arrays.stream(values()).filter(v -> v.getVersion().equals(version)).findFirst().orElse(null);
    }
}
