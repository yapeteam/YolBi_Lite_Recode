package cn.yapeteam.loader;

public class Natives {
    public static native void SetWindowsTransparent(boolean transparent, String windowTitle);

    public static native void SetLeftMouse(boolean pressed);

    public static native void SetRightMouse(boolean pressed);

    public static native void SetKey(int key, boolean pressed);
}
