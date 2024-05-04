package cn.yapeteam.agent;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

        String yolbi_dir = new File(System.getProperty("user.home"), ".yolbi").getAbsolutePath();
        for (File file : Objects.requireNonNull(new File(yolbi_dir).listFiles()))
            if (file.getName().endsWith(".jar") && !file.getName().equals("injection.jar")) {
                System.out.println(file.getAbsolutePath());
                loadJar(loader, file);
            }
        Class.forName("cn.yapeteam.loader.InstrumentationWrapper", true, loader).getConstructor(Instrumentation.class).newInstance(instrumentation);
        Class.forName("cn.yapeteam.loader.Loader", true, loader).getMethod("preload", String.class).invoke(null, yolbi_dir);
        loadJar(loader, new File(yolbi_dir, "injection.jar"));
        Class.forName("cn.yapeteam.yolbi.Loader", true, loader).getMethod("start").invoke(null);
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws Throwable {
        inject(instrumentation);
    }

    private static byte[] readStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }

    public static void mkdir(File file) {
        if (null == file || file.exists())
            return;
        mkdir(file.getParentFile());
        boolean ignored = file.mkdir();
    }

    public static void unzip(InputStream zipFile, File desDir) throws Exception {
        boolean ignored = desDir.mkdir();
        ZipInputStream zipInputStream = new ZipInputStream(zipFile);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            String unzipFilePath = desDir.getAbsolutePath() + File.separator + zipEntry.getName();
            if (zipEntry.isDirectory())
                mkdir(new File(unzipFilePath));
            else {
                File file = new File(unzipFilePath);
                mkdir(file.getParentFile());
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(unzipFilePath)));
                byte[] bytes = new byte[1024];
                int readLen;
                while ((readLen = zipInputStream.read(bytes)) != -1)
                    bufferedOutputStream.write(bytes, 0, readLen);
                bufferedOutputStream.close();
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    public static void premain(String args, Instrumentation instrumentation) throws Throwable {
        boolean isDebug = args != null && args.equals("debug");
        if (isDebug) {
            String yolbi_dir = new File(System.getProperty("user.home"), ".yolbi").getAbsolutePath();
            File injectionJar = new File(new File(new File(".").getAbsolutePath()).getParentFile().getParentFile().getParentFile(), "build/injector.jar");
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(injectionJar.toPath()));
            ZipEntry entry;
            byte[] zipData = null;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals("injection.zip")) {
                    zipData = readStream(zipInputStream);
                    break;
                }
            }
            zipInputStream.close();
            if (zipData == null) {
                System.err.println("Failed to find injection.zip in " + injectionJar.getAbsolutePath());
                return;
            }
            unzip(new ByteArrayInputStream(zipData), new File(yolbi_dir));
        }
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
