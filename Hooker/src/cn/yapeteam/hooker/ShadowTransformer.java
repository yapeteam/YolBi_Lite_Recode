package cn.yapeteam.hooker;

import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.ymixin.utils.Mapper;
import lombok.AllArgsConstructor;
import org.objectweb.asm_9_2.Type;
import org.objectweb.asm_9_2.tree.*;

import java.util.ArrayList;

import static cn.yapeteam.ymixin.utils.ASMUtils.getAnnotationValue;
import static cn.yapeteam.ymixin.utils.ASMUtils.slash;

public class ShadowTransformer {
    @AllArgsConstructor
    public static class Name_Desc {
        public String name, desc;
    }

    public static ClassNode transform(ClassNode node) {
        ArrayList<Name_Desc> methodShadows = new ArrayList<>();
        ArrayList<Name_Desc> fieldShadows = new ArrayList<>();
        String targetName = null;
        if (node.visibleAnnotations != null) {
            Type type = getAnnotationValue(
                    node.visibleAnnotations.stream()
                            .filter(a -> a.desc.contains(slash(Mixin.class.getName())))
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
