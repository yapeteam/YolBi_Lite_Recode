package cn.yapeteam.loader;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.ymixin.annotations.DontMap;
import cn.yapeteam.ymixin.annotations.Super;
import cn.yapeteam.ymixin.utils.DescParser;
import cn.yapeteam.ymixin.utils.Mapper;
import org.objectweb.asm_9_2.Handle;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassMapper {
    public enum MapMode {
        Super, Interface, Annotation, Method, Field, Mixed
    }

    public static ClassNode map(ClassNode node, MapMode mode) throws Throwable {
        if (Mapper.getMode() == Mapper.Mode.None || DontMap.Helper.hasAnnotation(node)) return node;
        Logger.info("Mapping class {}", node.name);
        if (mode == MapMode.Super || mode == MapMode.Mixed)
            node.superName = Mapper.getObfClass(node.superName);
        if (mode == MapMode.Interface || mode == MapMode.Mixed) {
            List<String> interfaces = new ArrayList<>();
            for (String anInterface : node.interfaces)
                interfaces.add(Mapper.getObfClass(anInterface));
            node.interfaces = interfaces;
        }
        if (mode == MapMode.Annotation || mode == MapMode.Mixed)
            if (node.visibleAnnotations != null)
                for (AnnotationNode visibleAnnotation : node.visibleAnnotations) {
                    if (visibleAnnotation.values == null) continue;
                    List<Object> values = new ArrayList<>();
                    for (int i = 0; i < visibleAnnotation.values.size(); i++) {
                        Object aValue = visibleAnnotation.values.get(i);
                        if (aValue instanceof Type) {
                            Type type = (Type) aValue;
                            String name = type.getClassName();
                            int count = 0;
                            if (name.contains("[]"))
                                while (name.contains("[]")) {
                                    name = replaceFirst(name, "[]", "");
                                    count++;
                                }
                            StringBuilder builder = new StringBuilder();
                            for (int j = 0; j < count; j++)
                                builder.append("[");
                            aValue = Type.getType(builder + "L" + name.replace(name, Mapper.getObfClass(name)).replace('.', '/') + ";");
                        }
                        values.add(aValue);
                    }
                    visibleAnnotation.values = values;
                }
        if (mode == MapMode.Method || mode == MapMode.Mixed)
            for (MethodNode method : node.methods)
                method(method, node);
        if (mode == MapMode.Field || mode == MapMode.Mixed)
            for (FieldNode field : node.fields)
                field(field);
        return node;
    }

    @SuppressWarnings("DuplicatedCode")
    private static String splitDesc(String desc) {
        char[] chars = desc.toCharArray();
        ArrayList<String> types = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == 'L') {
                i++;
                while (chars[i] != ';') {
                    builder.append(chars[i]);
                    i++;
                }
                types.add(builder.toString());
                builder = new StringBuilder();
            }
        }
        for (String type : types) {
            builder.append(type).append(';');
        }
        String result = builder.toString();
        result = types.size() == 1 ? result.replace(";", "") : result;
        return result;
    }

    public static String replaceFirst(String string, CharSequence target, CharSequence replacement) {
        return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(string).replaceFirst(Matcher.quoteReplacement(replacement.toString()));
    }


    private static boolean hasType(String type) {
        return Mapper.getVanilla().stream().anyMatch(m -> m.getType() == Mapper.Type.Class && m.getName().equals(type));
    }

    public static void method(MethodNode source, ClassNode parent) throws Throwable {
        if (source.visibleAnnotations != null) {
            for (AnnotationNode visibleAnnotation : source.visibleAnnotations) {
                if (Super.Helper.isAnnotation(visibleAnnotation)) {
                    source.name = Mapper.mapWithSuper(parent.superName, source.name, null, Mapper.Type.Method);
                    break;
                }
                if (DontMap.Helper.isAnnotation(visibleAnnotation)) return;
            }
        }
        for (String name : splitDesc(source.desc).split(";"))
            source.desc = replaceFirst(source.desc, name, Mapper.getObfClass(name));
        for (AbstractInsnNode instruction : source.instructions) {
            if (instruction instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                if (hasType(methodInsnNode.owner)) {
                    methodInsnNode.name = Mapper.mapMethodWithSuper(methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc);
                    methodInsnNode.owner = Mapper.getObfClass(methodInsnNode.owner);
                }
                if (hasType(splitDesc(methodInsnNode.desc))) methodInsnNode.desc = desc(methodInsnNode.desc);
                for (String name : splitDesc(methodInsnNode.desc).split(";"))
                    methodInsnNode.desc = replaceFirst(methodInsnNode.desc, name, Mapper.getObfClass(name));
            } else if (instruction instanceof TypeInsnNode) {
                TypeInsnNode typeInsnNode = (TypeInsnNode) instruction;
                typeInsnNode.desc = Mapper.map(null, typeInsnNode.desc, null, Mapper.Type.Class);
            } else if (instruction instanceof FieldInsnNode) {
                FieldInsnNode fieldInsnNode = (FieldInsnNode) instruction;
                if (hasType(fieldInsnNode.owner)) {
                    fieldInsnNode.name = Mapper.mapFieldWithSuper(fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
                    fieldInsnNode.owner = Mapper.map(null, fieldInsnNode.owner, null, Mapper.Type.Class);
                }
                if (hasType(splitDesc(fieldInsnNode.desc)))
                    fieldInsnNode.desc = desc(fieldInsnNode.desc);
            } else if (instruction instanceof LdcInsnNode) {
                LdcInsnNode ldcInsnNode = (LdcInsnNode) instruction;
                if (ldcInsnNode.cst instanceof Type) {
                    Type type = (Type) ldcInsnNode.cst;
                    String name = type.getClassName();
                    int count = 0;
                    if (name.contains("[]"))
                        while (name.contains("[]")) {
                            name = replaceFirst(name, "[]", "");
                            count++;
                        }
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < count; i++)
                        builder.append("[");
                    ldcInsnNode.cst = Type.getType(builder + "L" + name.replace(name, Mapper.getObfClass(name)).replace('.', '/') + ";");
                }
            } else if (instruction instanceof InvokeDynamicInsnNode) {
                InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) instruction;
                invokeDynamicInsnNode.desc = DescParser.mapDesc(invokeDynamicInsnNode.desc);
                for (int i = 0; i < invokeDynamicInsnNode.bsmArgs.length; i++) {
                    Object bsmArg = invokeDynamicInsnNode.bsmArgs[i];
                    if (bsmArg instanceof Handle) {
                        Handle handle = (Handle) bsmArg;
                        invokeDynamicInsnNode.bsmArgs[i] = new Handle(handle.getTag(), Mapper.getObfClass(handle.getOwner()), Mapper.map(handle.getOwner(), handle.getName(), handle.getDesc(), Mapper.Type.Method), DescParser.mapDesc(handle.getDesc()), handle.isInterface());
                    } else if (bsmArg instanceof Type) {
                        Type type = (Type) bsmArg;
                        String desc = type.toString();
                        invokeDynamicInsnNode.bsmArgs[i] = Type.getType(DescParser.mapDesc(desc));
                    }
                }
            }
        }
    }

    public static void field(FieldNode node) {
        for (String name : splitDesc(node.desc).split(";"))
            node.desc = replaceFirst(node.desc, name, Mapper.getObfClass(name));
    }

    private static String desc(String desc) {
        for (String name : splitDesc(desc).split(";"))
            desc = replaceFirst(desc, name, Mapper.getObfClass(name));
        return desc;
    }
}
