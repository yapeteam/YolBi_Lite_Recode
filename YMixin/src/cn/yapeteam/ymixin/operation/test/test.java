package cn.yapeteam.ymixin.operation.test;

import cn.yapeteam.ymixin.Transformer;
import cn.yapeteam.ymixin.YMixin;
import cn.yapeteam.ymixin.operation.impl.InjectOperation;
import cn.yapeteam.ymixin.utils.Mapper;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class test {
    static class CustomLoader extends ClassLoader {
        public Class<?> load(byte[] bytes) {
            return defineClass(null, bytes, 0, bytes.length);
        }
    }

    public static byte[] readStream(InputStream inStream) throws IOException {
        val outStream = new ByteArrayOutputStream();
        val buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }

    public static void main(String[] args) throws Throwable {
        //val bytes1 = readStream(test.class.getResourceAsStream("/cn/yapeteam/ymixin/operation/test/target.class"));
        //ClassNode node = ASMUtils.node(bytes1);
        //node.methods.stream().filter(methodNode -> methodNode.name.equals("func")).findFirst().ifPresent(m -> {
        //    LabelNode labelNode = (LabelNode) m.instructions.getLast();
        //    LabelNode ret = new LabelNode();
        //    InsnList list = new InsnList();
        //    boolean added = false;
        //    for (AbstractInsnNode instruction : m.instructions) {
        //        list.add(instruction);
        //        if (instruction instanceof MethodInsnNode && !added) {
        //            list.add(new JumpInsnNode(Opcodes.GOTO, labelNode));
        //            list.add(ret);
        //            added = true;
        //        }
        //    }
        //    list.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        //    list.add(new VarInsnNode(Opcodes.FLOAD, 0));
        //    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V"));
        //    list.add(new JumpInsnNode(Opcodes.GOTO, ret));
        //    m.instructions = list;
        //});
        //byte[] bytes = ASMUtils.rewriteClass(node);
        //Files.write(new File("target.class").toPath(), bytes);
        //new CustomLoader().load(bytes).getMethod("func").invoke(null);
        YMixin.init(
                name -> {
                    try {
                        return Class.forName(name.replace("/", "."), true, ClassLoader.getSystemClassLoader());
                    } catch (Throwable e) {
                        return null;
                    }
                }, null
        );
        Mapper.setMode(Mapper.Mode.None);
        Transformer transformer = new Transformer((name) -> readStream(InjectOperation.class.getResourceAsStream("/" + name.getName().replace('.', '/') + ".class")));
        val bytes1 = readStream(InjectOperation.class.getResourceAsStream("/cn/yapeteam/ymixin/operation/test/source.class"));
        transformer.addMixin(bytes1);
        byte[] bytes = transformer.transform().get("cn.yapeteam.ymixin.operation.test.target");
        Files.write(new File("target.class").toPath(), bytes);
        new CustomLoader().load(bytes).getMethod("func").invoke(null);
    }
}
