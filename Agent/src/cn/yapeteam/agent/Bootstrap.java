package cn.yapeteam.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

@SuppressWarnings("unused")
public class Bootstrap {
    private static void inject(Instrumentation instrumentation) throws Throwable {
        URLClassLoader loader = null;
        for (Object o : Thread.getAllStackTraces().keySet().toArray()) {
            Thread thread = (Thread) o;
            if (thread.getName().equals("Client thread")) {
                loader = (URLClassLoader) thread.getContextClassLoader();
                Thread.currentThread().setContextClassLoader(loader);
                break;
            }
        }
        File file = new File(new File(new File(".").getAbsolutePath()).getParentFile().getParentFile().getParentFile(), "build/injector.jar");
        loadJar(loader, file);
        Class.forName("cn.yapeteam.injector.Main", true, loader)
                .getMethod("main", String[].class)
                .invoke(null, (Object) new String[]{ManagementFactory.getRuntimeMXBean().getName().split("@")[0]});
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws Throwable {
        inject(instrumentation);
    }

    public static void premain(String args, Instrumentation instrumentation) {
        new Thread(() -> {
            try {
                boolean isRunning = false;
                while (!isRunning) {
                    for (Object o : Thread.getAllStackTraces().keySet().toArray()) {
                        Thread thread = (Thread) o;
                        if (thread.getName().equals("Client thread")) {
                            isRunning = true;
                            break;
                        }
                    }
                }
                Thread.sleep(5000);
                inject(instrumentation);

            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static void loadJar(URLClassLoader urlClassLoader, File jar) throws Throwable {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, jar.toURI().toURL());
    }
}
