package cn.yapeteam.loader.mixin;

import cn.yapeteam.loader.mixin.operation.Operation;
import cn.yapeteam.loader.mixin.operation.impl.InjectOperation;
import cn.yapeteam.loader.mixin.operation.impl.OverwriteOperation;
import cn.yapeteam.loader.utils.ASMUtils;
import cn.yapeteam.loader.utils.ClassUtils;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Transformer {
    private final ClassProvider provider;
    private final ArrayList<Mixin> mixins;
    private final ArrayList<Operation> operations;

    @Getter
    private byte[] oldBytes;

    public Transformer(ClassProvider classProvider) {
        this.provider = classProvider;
        this.mixins = new ArrayList<>();
        this.operations = new ArrayList<>();
        operations.add(new InjectOperation());
        operations.add(new OverwriteOperation());
    }

    public void addMixin(Class<?> theClass) throws Throwable {
        byte[] bytes = ClassUtils.getClassBytes(theClass.getName());
        ClassNode source = ASMUtils.node(bytes);
        mixins.add(new Mixin(source, theClass, provider));
    }

    public Map<String, byte[]> transform() throws IOException {
        Map<String, byte[]> classMap = new HashMap<>();
        for (Mixin mixin : mixins) {
            for (Operation operation : operations)
                operation.dispose(mixin);
            String name = mixin.getTarget().name.replace('/', '.');
            oldBytes = getClassBytes(mixin.getTarget().getClass());
            byte[] class_bytes = ASMUtils.rewriteClass(mixin.getTarget());
            classMap.put(name, class_bytes);
        }
        return classMap;
    }

    public Map<String, byte[]> transformoldbytes() throws IOException {
        Map<String, byte[]> classMap = new HashMap<>();
        for (Mixin mixin : mixins) {
            for (Operation operation : operations)
                operation.dispose(mixin);
            String name = mixin.getTarget().name.replace('/', '.');
            oldBytes = getClassBytes(mixin.getTarget().getClass());
            byte[] class_bytes = ASMUtils.rewriteClass(mixin.getTarget());
            classMap.put(name, oldBytes);
        }
        return classMap;
    }


    public static byte[] getClassBytes(Class<?> c) throws IOException {
        String className = c.getName();
        String classAsPath = className.replace('.', '/') + ".class";
        InputStream stream = c.getClassLoader().getResourceAsStream(classAsPath);
        return stream == null ? null : IOUtils.toByteArray(stream);
    }

}
