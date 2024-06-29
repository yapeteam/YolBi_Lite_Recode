package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.StreamUtils;
import cn.yapeteam.ymixin.annotations.DontMap;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.utils.ASMUtils;
import lombok.val;
import lombok.var;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("SameParameterValue")
public class JarMapper {
    private static void write(String name, byte[] bytes, ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(bytes);
        zos.closeEntry();
    }

    public static void dispose(File file, String jarName, ClassMapper.MapMode mode) throws Throwable {
        SocketSender.send("S1");
        var all = 0;
        try (val zis = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            while (zis.getNextEntry() != null) all++;
        }
        val zos = new ZipOutputStream(Files.newOutputStream(Paths.get(Loader.YOLBI_DIR + "/" + jarName)));
        zos.setMethod(ZipOutputStream.DEFLATED);
        zos.setLevel(Deflater.BEST_COMPRESSION);
        try (val zis = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            ZipEntry se;
            int count = 0;
            while ((se = zis.getNextEntry()) != null) {
                count++;
                int finalCount = count;
                int finalAll = all;
                new Thread(() -> SocketSender.send("P1" + " " + (float) finalCount / finalAll * 100f)).start();
                var bytes = StreamUtils.readStream(zis);
                if (!se.isDirectory() && se.getName().endsWith(".class") && se.getName().startsWith("cn/yapeteam/")) {
                    val node = ASMUtils.node(bytes);
                    if (DontMap.Helper.hasAnnotation(node)) {
                        write(se.getName(), bytes, zos);
                        System.out.println("Skipping class: " + se.getName());
                        continue;
                    }
                    bytes = ASMUtils.rewriteClass(ClassMapper.map(node, mode));
                    if (node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(a -> a.desc.contains(ASMUtils.slash(Mixin.class.getName())))) {
                        Logger.info("Mapping mixin class: {}", se.getName());
                        ResourceManager.resources.res.put(se.getName().replace(".class", "").replace('/', '.'), bytes);
                    }
                    write(se.getName(), bytes, zos);
                } else if (!se.isDirectory()) write(se.getName(), bytes, zos);
            }
            zos.close();
        }
    }
}
