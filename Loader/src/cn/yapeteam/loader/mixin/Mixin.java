package cn.yapeteam.loader.mixin;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ASMUtils;
import lombok.Getter;
import org.objectweb.asm_9_2.tree.ClassNode;

@Getter
public class Mixin {
    private final ClassNode source;
    private ClassNode target;
    private final String targetName;

    public Mixin(ClassNode source, Class<?> theClass, ClassProvider provider) throws Throwable {
        this.source = source;
        Class<?> targetClass = theClass.getAnnotation(cn.yapeteam.loader.mixin.annotations.Mixin.class).value();
        targetName = targetClass.getName().replace('.', '/');
        Logger.info("Loading mixin {}, size: {} bytes", source.name, provider.getClassBytes(targetClass).length);
        int tries = 0;
        while (target == null && tries < 5) {
            try {
                target = ASMUtils.node(provider.getClassBytes(targetClass));
            } catch (IllegalArgumentException ignored) {
                tries++;
                Thread.sleep(500);
            }
        }
        if (target == null)
            Logger.error("Failed to load target class {} for mixin {}", targetClass.getName(), source.name);
    }
}
