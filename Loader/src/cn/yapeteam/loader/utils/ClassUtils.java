package cn.yapeteam.loader.utils;

import cn.yapeteam.loader.BootStrap;
import cn.yapeteam.loader.ResourceManager;

public class ClassUtils {
    public static Class<?> getClass(String name) {
        name = name.replace('/', '.');
        try {
            return Class.forName(name, true, BootStrap.client_thread.getContextClassLoader());
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public static byte[] getClassBytes(String name) {
        return ResourceManager.resources.get(name.replace('.', '/') + ".class");
    }
}
