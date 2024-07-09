package cn.yapeteam.loader;

/**
 * native used
 */
public class NativeWrapper extends JVMTIWrapper {
    public native int redefineClass(Class<?> clazz, byte[] array);

    public native byte[] getClassBytes(Class<?> clazz);

    public native Class<?> defineClass(ClassLoader loader, byte[] array);

    public native Class<?> FindClass(String name, Object loader);
}
