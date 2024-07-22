package cn.yapeteam.builder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("unused")
public class Unzip {
    /**
     * 解压zip文件到指定目录
     * used by native code
     */
    public static void unzip(InputStream zipFile, File desDir) throws Exception {
        boolean ignored = desDir.mkdir();
        ZipInputStream zipInputStream = new ZipInputStream(zipFile);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            String unzipFilePath = desDir.getAbsolutePath() + File.separator + zipEntry.getName();
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
