package cn.yapeteam.loader.utils;

import cn.yapeteam.loader.logger.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm_9_2.ClassReader;
import org.objectweb.asm_9_2.ClassWriter;
import org.objectweb.asm_9_2.tree.AnnotationNode;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.util.ArrayList;

import static org.objectweb.asm_9_2.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm_9_2.ClassWriter.COMPUTE_MAXS;

public class ASMUtils {
    @Contract(pure = true)
    public static @NotNull String slash(@NotNull String s) {
        return s.replace('.', '/');
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

    public static String readClassName(byte[] bytes) {
        return new ClassReader(bytes).getClassName();
    }

    public static byte[] rewriteClass(@NotNull ClassNode node) {
        ClassWriter writer = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES) {
            @Override
            protected @NotNull String getCommonSuperClass(@NotNull String type1, @NotNull String type2) {
                try {
                    Logger.info("Getting common superclass for types: " + type1 + ", " + type2);
                    Class<?> class1 = ClassUtils.getClass(type1);
                    Class<?> class2 = ClassUtils.getClass(type2);
                    if (class1 != null && class2 != null) {
                        Logger.info("Classes loaded: " + class1.getName() + ", " + class2.getName());
                        if (class1.isAssignableFrom(class2)) {
                            Logger.info("Class1 is assignable from class2. Returning type1: " + type1);
                            return type1;
                        } else if (class2.isAssignableFrom(class1)) {
                            Logger.info("Class2 is assignable from class1. Returning type2: " + type2);
                            return type2;
                        } else if (!class1.isInterface() && !class2.isInterface()) {
                            do {
                                class1 = class1.getSuperclass();
                                Logger.info("Getting superclass of class1: " + class1.getName());
                            } while (!class1.isAssignableFrom(class2));
                            String superClass = class1.getName().replace('.', '/');
                            Logger.info("Found common superclass: " + superClass);
                            return superClass;
                        }
                    }
                } catch (Throwable ignored) {
                    Logger.error("Error while getting common superclass for types: " + type1 + ", " + type2, ignored);
                }
                Logger.info("Returning default superclass: java/lang/Object");
                return "java/lang/Object";
            }
        };
        node.accept(writer);
        return writer.toByteArray();
    }

    public static <T> T getAnnotationValue(AnnotationNode node, String name) {
        if (node != null)
            for (int i = 0; i < node.values.size(); i += 2) {
                if (node.values.get(i).equals(name)) {
                    Object obj = node.values.get(i + 1);
                    return (T) obj;
                }
            }
        return null;
    }

    public static @NotNull String @NotNull [] split(@NotNull String str, String splitter) {
        if (!str.contains(splitter))
            return new String[]{};
        ArrayList<String> result = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder passed = new StringBuilder();
        for (int i = 0; i < str.length() - (splitter.length() - 1); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < i + splitter.length(); j++)
                sb.append(str.charAt(j));
            if (sb.toString().equals(splitter)) {
                result.add(stringBuilder.toString());
                passed.append(stringBuilder);
                passed.append(splitter);
                stringBuilder = new StringBuilder();
                i += splitter.length();
            }
            if (i < str.length() - 1)
                stringBuilder.append(str.charAt(i));
        }
        String last = str.replace(passed.toString(), "");
        if (!last.isEmpty())
            result.add(last);
        return result.toArray(new String[0]);
    }
}
