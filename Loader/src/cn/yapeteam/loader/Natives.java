package cn.yapeteam.loader;

import org.lwjgl.opengl.Display;

public class Natives {
    public static boolean initialized = false;

    public static void Init() {
        if (!initialized)
            Init(Display.getTitle());
        initialized = true;
    }

    private static native void Init(String windowTitle);

    public static native void SetWindowsTransparent(boolean transparent, String windowTitle);

    public static native boolean IsMouseDown(int button);

    public static native void SetKeyBoard(int key, boolean pressed);

    public static native void SendLeft();

    public static native void SendRight();
}
