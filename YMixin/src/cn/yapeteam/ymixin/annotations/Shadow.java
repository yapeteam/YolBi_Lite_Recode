package cn.yapeteam.ymixin.annotations;

import cn.yapeteam.ymixin.utils.ASMUtils;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.ymixin.utils.Name_Desc;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Shadow {
    class Helper {
        public static boolean isAnnotation(@NotNull AnnotationNode node) {
            return node.desc.substring(1, node.desc.length() - 1).equals(ASMUtils.slash(Shadow.class.getName()));
        }

        public static boolean hasAnnotation(@NotNull MethodNode node) {
            return node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(Shadow.Helper::isAnnotation);
        }

        public static boolean hasAnnotation(@NotNull FieldNode node) {
            return node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(Shadow.Helper::isAnnotation);
        }
        public static ClassNode processShadow(ClassNode node) {
            // Process @Shadow
            ArrayList<Name_Desc> methodShadows = new ArrayList<>();
            ArrayList<Name_Desc> fieldShadows = new ArrayList<>();
            String targetName = null;
            if (node.visibleAnnotations != null) {
                Type type = ASMUtils.getAnnotationValue(
                        node.visibleAnnotations.stream()
                                .filter(a -> a.desc.contains(ASMUtils.slash(Mixin.class.getName())))
                                .findFirst().orElse(null), "value"
                );
                if (type != null) targetName = type.getClassName();
                if (targetName != null) {
                    for (MethodNode method : node.methods) {
                        if (Shadow.Helper.hasAnnotation(method))
                            methodShadows.add(new Name_Desc(method.name, method.desc));
                    }
                    for (FieldNode field : node.fields) {
                        if (Shadow.Helper.hasAnnotation(field))
                            fieldShadows.add(new Name_Desc(field.name, field.desc));
                    }
                    targetName = targetName.replace('.', '/');
                }
            }
            for (MethodNode method : node.methods) {
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                        if (methodShadows.stream().anyMatch(m -> m.name.equals(methodInsnNode.name) && m.desc.equals(methodInsnNode.desc)))
                            methodInsnNode.owner = Mapper.getFriendlyClass(targetName);
                    } else if (instruction instanceof FieldInsnNode) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) instruction;
                        if (fieldShadows.stream().anyMatch(m -> m.name.equals(fieldInsnNode.name) && m.desc.equals(fieldInsnNode.desc)))
                            fieldInsnNode.owner = Mapper.getFriendlyClass(targetName);
                    }
                }
            }
            return node;
        }
    }
}
