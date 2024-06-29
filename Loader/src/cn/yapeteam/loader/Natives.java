package cn.yapeteam.loader;

import org.lwjgl.opengl.Display;

/**
 * native used
 */
public class Natives {
    public static boolean initialized = false;

    public static void Init() {
        if (!initialized)
            Init(Display.getTitle());
        initialized = true;
    }

    private static native void Init(String windowTitle);

    public static native void SetWindowsTransparent(boolean transparent, String windowTitle);

    public static native boolean IsKeyDown(int button);

    /**
     * @param key Must be Virtual Key Code
     */
    public static native void SetKeyBoard(int key, boolean pressed);

    public static native void SendLeft(boolean pressed);

    public static native void SendRight(boolean pressed);
}
