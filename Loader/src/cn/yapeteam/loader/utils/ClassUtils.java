package cn.yapeteam.loader.utils;

import cn.yapeteam.loader.JVMTIWrapper;
import cn.yapeteam.loader.ResourceManager;

public class ClassUtils {
    public static Class<?> getClass(String name) {
        name = name.replace('.', '/');
        Class<?> clazz = null;
        if (JVMTIWrapper.instance != null) {
            try {
                clazz = JVMTIWrapper.instance.FindClass(name);
            } catch (Throwable ignored) {
            }
            if (clazz != null) return clazz;
        }
        name = name.replace('/', '.');
        try {
            clazz = Class.forName(name);
        } catch (Throwable ignored) {
        }
        return clazz;
    }

    public static byte[] getClassBytes(String name) {
        return ResourceManager.resources.get(name.replace('.', '/') + ".class");
    }
}
