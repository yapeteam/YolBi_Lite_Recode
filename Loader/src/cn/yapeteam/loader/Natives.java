package cn.yapeteam.loader;

public class Natives {
    public static native void SetWindowsTransparent(boolean transparent, String windowTitle);

    public static native void SetKeyBoard(int key, boolean pressed);

    public static native void SetMouse(int button, boolean pressed);
}
