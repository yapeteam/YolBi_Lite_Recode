package cn.yapeteam.ymixin.operation.impl;

import cn.yapeteam.ymixin.Mixin;
import cn.yapeteam.ymixin.annotations.Redirect;
import cn.yapeteam.ymixin.operation.Operation;
import org.objectweb.asm_9_2.*;
import org.objectweb.asm_9_2.commons.AdviceAdapter;
import org.objectweb.asm_9_2.tree.ClassNode;
import org.objectweb.asm_9_2.tree.MethodNode;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RedirectOperation implements Operation {

    public static class RedirectClassVisitor extends ClassVisitor {
        private final Map<String, String> methodRedirects;

        public RedirectClassVisitor(ClassVisitor cv, Map<String, String> methodRedirects) {
            super(Opcodes.ASM9, cv);
            this.methodRedirects = methodRedirects;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new RedirectMethodVisitor(mv, methodRedirects);
        }
    }

    public static class RedirectMethodVisitor extends AdviceAdapter {
        private final String targetMethodName;
        private final String targetMethodDesc;

        protected RedirectMethodVisitor(MethodVisitor mv, int access, String name, String desc, String targetMethodName, String targetMethodDesc) {
            super(Opcodes.ASM9, mv, access, name, desc);
            this.targetMethodName = targetMethodName;
            this.targetMethodDesc = targetMethodDesc;
        }

        @Override
        protected void onMethodEnter() {
            if (name.equals(targetMethodName) && desc.equals(targetMethodDesc)) {
                // 在这里插入你想要重定向的方法逻辑
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/example/MyClass", "myRedirectMethod", "()V", false);
                // 跳过原始方法调用
                mv.visitInsn(Opcodes.RETURN);
            }
        }
    }

    @Override
    public void dispose(Mixin mixin) {
        ClassNode source = mixin.getSource();
        ClassNode target = mixin.getTarget();
        List<MethodNode> redirections = source.methods.stream()
                .filter(Redirect.Helper::hasAnnotation)
                .collect(Collectors.toList());
        for (MethodNode redirection : redirections) {
            Redirect info = Redirect.Helper.getAnnotation(redirection);

        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> methodRedirects = new HashMap<>();
        methodRedirects.put("java/io/PrintStream.println (F)V", "com/example/Logger.log(F)V");

        ClassReader classReader = new ClassReader("cn/yapeteam/ymixin/operation/test/target");
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        ClassVisitor classVisitor = new RedirectClassVisitor(classWriter, methodRedirects);
        classReader.accept(classVisitor, 0);

        byte[] modifiedClass = classWriter.toByteArray();
        Files.write(new File("target.class").toPath(), modifiedClass);
    }
}
