package cn.yapeteam.loader.mixin.operation.impl;

import cn.yapeteam.loader.Mapper;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.mixin.Mixin;
import cn.yapeteam.loader.mixin.annotations.Modify;
import cn.yapeteam.loader.mixin.operation.Operation;
import org.objectweb.asm_9_2.tree.AbstractInsnNode;
import org.objectweb.asm_9_2.tree.ClassNode;
import org.objectweb.asm_9_2.tree.LdcInsnNode;
import org.objectweb.asm_9_2.tree.MethodNode;

import java.util.List;
import java.util.stream.Collectors;

public class ModifyOperation implements Operation {
    @Override
    public void dispose(Mixin mixin) {
        ClassNode source = mixin.getSource();
        ClassNode target = mixin.getTarget();
        List<MethodNode> modifications = source.methods.stream()
                .filter(Modify.Helper::hasAnnotation)
                .collect(Collectors.toList());
        for (MethodNode modification : modifications) {
            Modify info = Modify.Helper.getAnnotation(modification);
            if (info == null) continue;
            MethodNode targetMethod = Operation.findTargetMethod(target.methods, mixin.getTargetName(), info.method(), info.desc());
            if (targetMethod == null) {
                Logger.error("No method found: {} in {}", Mapper.mapWithSuper(mixin.getTargetName(), info.method(), info.desc(), Mapper.Type.Method) + info.desc(), target.name);
                return;
            }

            // Find the LdcInsnNode that loads the constant 3.0D onto the operand stack
            LdcInsnNode ldc = null;
            for (int i = 0; i < targetMethod.instructions.size(); ++i) {
                AbstractInsnNode x = targetMethod.instructions.get(i);

                if (x instanceof LdcInsnNode) {
                    LdcInsnNode t = (LdcInsnNode) x;

                    if (t.cst instanceof Double && ((Double) t.cst) == 3.0) {
                        ldc = t;
                    }
                }
            }

            // Replace the LdcInsnNode with a new LdcInsnNode that loads the constant 6.0D onto the operand stack
            if (ldc != null) {
                LdcInsnNode newLdc = new LdcInsnNode(6.0);
                targetMethod.instructions.insert(ldc, newLdc);
                targetMethod.instructions.remove(ldc);
            }
        }
    }
}
