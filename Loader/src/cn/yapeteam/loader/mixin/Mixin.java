package cn.yapeteam.loader.mixin;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ASMUtils;
import lombok.Getter;
import org.objectweb.asm_9_2.tree.ClassNode;

@Getter
public class Mixin {
    private final ClassNode source;
    private final ClassNode target;
    private final String targetName;

    public Mixin(ClassNode source, Class<?> theClass, ClassProvider provider) throws Throwable {
        this.source = source;
        Class<?> targetClass = theClass.getAnnotation(cn.yapeteam.loader.mixin.annotations.Mixin.class).value();
        targetName = targetClass.getName().replace('.', '/');
        Logger.info("Loading mixin {}, size: {} bytes", source.name, provider.getClassBytes(targetClass).length);
        target = ASMUtils.node(provider.getClassBytes(targetClass));
    }
}
