package cn.yapeteam.loader;

import java.lang.management.ManagementFactory;

/**
 * native used
 */
public class Natives {
    public static boolean initialized = false;

    public static void Init() {
        if (!initialized)
            Init(Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]));
        initialized = true;
    }

    private static native void Init(int pid);

    public static native void SetWindowsTransparent(boolean transparent, String windowTitle);

    public static native boolean IsKeyDown(int button);

    /**
     * @param key Must be Virtual Key Code
     */
    public static native void SetKeyBoard(int key, boolean pressed);

    public static native void SendLeft(boolean pressed);

    public static native void SendRight(boolean pressed);
}
