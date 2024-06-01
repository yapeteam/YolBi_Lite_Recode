package cn.yapeteam.loader.mixin;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.mixin.operation.Operation;
import cn.yapeteam.loader.mixin.operation.impl.InjectOperation;
import cn.yapeteam.loader.mixin.operation.impl.ModifyOperation;
import cn.yapeteam.loader.mixin.operation.impl.OverwriteOperation;
import cn.yapeteam.loader.utils.ASMUtils;
import cn.yapeteam.loader.utils.ClassUtils;
import lombok.Getter;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Transformer {
    private final ClassProvider provider;
    private final ArrayList<Mixin> mixins;
    private final ArrayList<Operation> operations;
    private final Map<String, byte[]> oldBytes = new HashMap<>();

    public Transformer(ClassProvider classProvider) {
        this.provider = classProvider;
        this.mixins = new ArrayList<>();
        this.operations = new ArrayList<>();
        operations.add(new InjectOperation());
        operations.add(new OverwriteOperation());
        operations.add(new ModifyOperation());
    }

    public void addMixin(Class<?> theClass) throws Throwable {
        byte[] bytes = ClassUtils.getClassBytes(theClass.getName());
        ClassNode source = ASMUtils.node(bytes);
        mixins.add(new Mixin(source, theClass, provider));
    }

    public Map<String, byte[]> transform() {
        Map<String, byte[]> classMap = new HashMap<>();
        oldBytes.clear();
        for (Mixin mixin : mixins) {
            String name = mixin.getTarget().name.replace('/', '.');
            oldBytes.put(name, mixin.getTargetOldBytes());
            for (Operation operation : operations)
                operation.dispose(mixin);
            try {
                byte[] class_bytes = ASMUtils.rewriteClass(mixin.getTarget());
                classMap.put(name, class_bytes);
            } catch (Throwable e) {
                Logger.error("Failed to transform class " + name, e);
                Logger.exception(e);
            }
        }
        return classMap;
    }
}
