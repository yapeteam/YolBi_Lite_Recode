package cn.yapeteam.loader.mixin.operation.impl;

import cn.yapeteam.loader.Mapper;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.mixin.Mixin;
import cn.yapeteam.loader.mixin.annotations.Modify;
import cn.yapeteam.loader.mixin.operation.Operation;
import org.objectweb.asm_9_2.tree.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm_9_2.Opcodes.INVOKEVIRTUAL;


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

            try {
                Class<?> clazz = Class.forName(info.replacepath().replace("/", "."));
                // Check if the method exists
                if (Arrays.stream(clazz.getMethods()).anyMatch(method -> method.getName().equals(info.replacementfunc()))) {
                    // Replace the LdcInsnNode with a new MethodInsnNode that calls event.getReach()
//                    if (ldc != null) {
//                        MethodInsnNode newInsn = new MethodInsnNode(INVOKEVIRTUAL, info.replacepath(), info.replacementfunc(), info.funcdesc(), false);
//                        targetMethod.instructions.insert(ldc, newInsn);
//                        targetMethod.instructions.remove(ldc);
//                        Logger.info("Successfully modified method " + info.method());
//                    }
                    // Replace the LdcInsnNode with a new LdcInsnNode that loads the constant 6.0D onto the operand stack
                    if (ldc != null) {
//                        LdcInsnNode newLdc = new LdcInsnNode(6.0);
                        MethodInsnNode newInsn = new MethodInsnNode(INVOKEVIRTUAL, "cn/yapeteam/yolbi/event/impl/player/EventMouseOver", "getReach", "()F", false);
                        targetMethod.instructions.insert(ldc, newInsn);
//                        targetMethod.instructions.remove(ldc);
                    }
                } else {
                    Logger.info("Method " + info.replacementfunc() + " does not exist in class " + info.replacepath());
                }
            } catch (ClassNotFoundException e) {
                Logger.info("Class " + info.replacepath() + " does not exist");
            }
        }
    }
}
