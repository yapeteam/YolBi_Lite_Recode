package net.montoyo.mcef.utilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Util {
    /**
     * Calls "close" on the specified object without throwing any exceptions.
     * This is usefull with input and output streams.
     *
     * @param o The object to call close on.
     */
    public static void close(Object o) {
        try {
            o.getClass().getMethod("close").invoke(o);
        } catch (Throwable t) {
        }
    }

    /**
     * Same as {@link Files#isSameFile(Path, Path)} but if an {@link IOException} is thrown,
     * return false.
     *
     * @param p1 Path 1
     * @param p2 Path 2
     * @return true if the paths are the same, false if they are not or if an exception is thrown during the comparison
     */
    public static boolean isSameFile(Path p1, Path p2) {
        try {
            return Files.isSameFile(p1, p2);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Same as {@link System#getenv(String)}, but if no such environment variable is
     * defined, will return an empty string instead of null.
     *
     * @param name Name of the environment variable to get
     * @return The value of this environment variable (may be empty but never null)
     */
    public static String getenv(String name) {
        String ret = System.getenv(name);
        return ret == null ? "" : ret;
    }

    /**
     * 解压zip文件到指定目录
     */
    public static void unzip(String zipFile, String desDir) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(zipFile)));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            String unzipFilePath = desDir + File.separator + zipEntry.getName();
            File file = new File(unzipFilePath);
            if (zipEntry.isDirectory())
                mkdir(new File(unzipFilePath));
            else if (!file.exists()) {
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

    public static void mkdir(File file) {
        if (null == file || file.exists())
            return;
        mkdir(file.getParentFile());
        boolean ignored = file.mkdir();
    }
}
