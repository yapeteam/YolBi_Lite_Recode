package org.cef;

public class OS {
    private static OSType osType = OSType.OSUndefined;

    public enum OSType {
        OSUndefined,
        OSLinux,
        OSWindows,
        OSMacintosh,
        OSUnknown
    }

    public static boolean isWindows() {
        return getOSType() == OSType.OSWindows;
    }

    public static boolean isMacintosh() {
        return getOSType() == OSType.OSMacintosh;
    }

    public static boolean isLinux() {
        return getOSType() == OSType.OSLinux;
    }

    public static OSType getOSType() {
        if (osType == OSType.OSUndefined) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows")) {
                osType = OSType.OSWindows;
            } else if (os.startsWith("linux")) {
                osType = OSType.OSLinux;
            } else if (os.startsWith("mac")) {
                osType = OSType.OSMacintosh;
            } else {
                osType = OSType.OSUnknown;
            }
        }
        return osType;
    }
}
