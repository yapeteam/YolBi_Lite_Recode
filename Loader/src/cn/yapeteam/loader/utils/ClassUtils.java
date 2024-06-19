package cn.yapeteam.loader.utils;

import cn.yapeteam.loader.BootStrap;
import cn.yapeteam.loader.JVMTIWrapper;
import cn.yapeteam.loader.ResourceManager;

public class ClassUtils {
    public static Class<?> getClass(String name) {
        Class<?> clazz = null;
        name = name.replace('/', '.');
        try {
            clazz = Class.forName(name, true, BootStrap.client_thread.getContextClassLoader());
        } catch (ClassNotFoundException ignored) {
        }
        if (clazz != null) return clazz;
        // if (JVMTIWrapper.instance != null) {
        //     try {
        //         clazz = JVMTIWrapper.instance.FindClass(name, BootStrap.client_thread.getContextClassLoader());
        //     } catch (Throwable ignored) {
        //     }
        // }
        // if (clazz != null) return clazz;
        return null;
    }

    public static byte[] getClassBytes(String name) {
        return ResourceManager.resources.get(name.replace('.', '/') + ".class");
    }
}
