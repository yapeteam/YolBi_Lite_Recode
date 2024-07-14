package cn.yapeteam.definer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.objectweb.asm_9_2.ClassReader;
import org.objectweb.asm_9_2.ClassVisitor;
import org.objectweb.asm_9_2.MethodVisitor;
import org.objectweb.asm_9_2.Opcodes;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarLoader {
    public static void main(String[] args) {
        loadJar("deps/flatlaf.jar", ClassLoader.getSystemClassLoader());
    }

    private static byte[] readStream(InputStream inStream) throws Exception {
        val outStream = new ByteArrayOutputStream();
        val buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }

    public static ClassNode node(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            ClassReader reader = new ClassReader(bytes);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            return node;
        }

        return null;
    }

    public static native Class<?> defineClass(ClassLoader loader, byte[] bytes);

    @AllArgsConstructor
    static class Pair {
        public ClassNode node;
        public byte[] bytes;
    }

    private static final CopyOnWriteArrayList<Pair> classes = new CopyOnWriteArrayList<>();

    @SuppressWarnings("unused")
    public static void loadJar(String jarPath, ClassLoader classLoader) {
        classes.clear();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(jarPath)))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    try {
                        byte[] classData = readStream(zipInputStream);
                        classes.add(new Pair(node(classData), classData));
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ArrayList<Pair> list = new ArrayList<>();
        long start = System.currentTimeMillis();
        sortClasses(list, null);
        System.out.println("sortClasses: " + (System.currentTimeMillis() - start) + "ms");
        for (Pair aClass : list)
            try {
                defineClass(classLoader, aClass.bytes);
                //System.out.println(aClass.node.name + ":" + (defineClass(classLoader, aClass.bytes) != null));
            } catch (Throwable ignored) {
            }
    }

    private static final ArrayList<String> added = new ArrayList<>();
    private static String lastClassName = null;
    private static final Pattern classNamePattern = Pattern.compile("[a-zA-Z0-9_/$]+");

    private static void sortClasses(ArrayList<Pair> list, String className) {
        if (className == null)
            added.clear();
        else lastClassName = className;
        if (className != null && (!classNamePattern.matcher(className).matches() || added.contains(className) || className.startsWith("java/")))
            return;
        for (Pair aClass : classes) {
            if (className == null || className.equals(aClass.node.name) && !added.contains(aClass.node.name)) {
                if (!aClass.node.name.equals(lastClassName))
                    aClass.node.accept(new ClassVisitor(Opcodes.ASM9) {
                        @Override
                        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                            if (superName != null && !superName.equals("java/lang/Object"))
                                sortClasses(list, superName);
                            for (String anInterface : interfaces)
                                sortClasses(list, anInterface);
                            super.visit(version, access, name, signature, superName, interfaces);
                        }

                        @Override
                        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                            if ("<clinit>".equals(name)) {
                                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                                    @Override
                                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                                        sortClasses(list, owner);
                                        sortClasses(list, descriptor.replace("[", "").substring(1).replace(";", ""));
                                        super.visitFieldInsn(opcode, owner, name, descriptor);
                                    }

                                    @Override
                                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                                        sortClasses(list, owner);
                                        sortClasses(list, descriptor.split("\\(")[0].replace("L", "").replace(";", "").replace("[", ""));
                                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                    }
                                };
                            }
                            return super.visitMethod(access, name, descriptor, signature, exceptions);
                        }
                    });
                added.add(aClass.node.name);
                list.add(aClass);
            }
        }
    }
}
