package cn.yapeteam.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

@SuppressWarnings("unused")
public class Bootstrap {
    private static void inject() throws Throwable {
        Class.forName("cn.yapeteam.injector.Main")
                .getMethod("main", String[].class)
                .invoke(null, (Object) new String[]{ManagementFactory.getRuntimeMXBean().getName().split("@")[0]});
    }

    public static void premain(String args, Instrumentation instrumentation) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                inject();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
