package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.StreamUtils;
import cn.yapeteam.ymixin.YMixin;
import cn.yapeteam.ymixin.annotations.DontMap;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.ymixin.utils.ASMUtils;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm_9_2.ClassWriter;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.objectweb.asm_9_2.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm_9_2.ClassWriter.COMPUTE_MAXS;

@SuppressWarnings("SameParameterValue")
public class JarMapper {
    private static void write(String name, byte[] bytes, ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(bytes);
        zos.closeEntry();
    }

    public static byte[] rewriteClass(@NotNull ClassNode node) {
        ClassWriter writer = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES) {
            @Override
            protected @NotNull String getCommonSuperClass(@NotNull String type1, @NotNull String type2) {
                if (type1.startsWith("cn/yapeteam/yolbi/") || type2.startsWith("cn/yapeteam/yolbi/"))
                    return "java/lang/Object";
                try {
                    Class<?> class1 = YMixin.classProvider.get(type1);
                    Class<?> class2 = YMixin.classProvider.get(type2);
                    if (class1 != null && class2 != null) {
                        if (class1.isAssignableFrom(class2)) {
                            return type1;
                        } else if (class2.isAssignableFrom(class1)) {
                            return type2;
                        } else if (!class1.isInterface() && !class2.isInterface()) {
                            do {
                                class1 = class1.getSuperclass();
                            } while (!class1.isAssignableFrom(class2));
                            return class1.getName().replace('.', '/');
                        }
                    }
                } catch (Throwable ignored) {
                }
                return "java/lang/Object";
            }
        };
        node.accept(writer);
        return writer.toByteArray();
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
                if (!se.isDirectory() && se.getName().endsWith(".class")) {
                    var node = ASMUtils.node(bytes);
                    if (DontMap.Helper.hasAnnotation(node)) {
                        write(se.getName(), bytes, zos);
                        Logger.info("Skipping class: {}", se.getName());
                        continue;
                    }
                    if (Mixin.Helper.hasAnnotation(node)) {
                        Logger.info("Mapping mixin class: {}", se.getName());
                        Shadow.Helper.processShadow(node);
                        ClassMapper.map(node, mode);
                        bytes = rewriteClass(node);
                        ResourceManager.resources.res.put(se.getName().substring(0, se.getName().length() - 6).replace('/', '.'), bytes);
                    } else {
                        Shadow.Helper.processShadowBySuper(node, node.superName);
                        ClassMapper.map(node, mode);
                        bytes = rewriteClass(node);
                    }
                    write(se.getName(), bytes, zos);
                } else if (!se.isDirectory()) write(se.getName(), bytes, zos);
            }
            zos.close();
        }
    }
}
