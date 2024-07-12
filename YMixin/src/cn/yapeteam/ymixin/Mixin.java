package cn.yapeteam.ymixin;

import cn.yapeteam.ymixin.utils.ASMUtils;
import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm_9_2.tree.ClassNode;

import static cn.yapeteam.ymixin.YMixin.Logger;

@Getter
public class Mixin {
    private byte[] targetOldBytes = null;
    private final ClassNode source;
    @Setter
    private ClassNode target;
    private final String targetName;

    public Mixin(ClassNode source, ClassBytesProvider provider) throws Throwable {
        this.source = source;
        Class<?> targetClass = null;
        while (targetClass == null) {
            try {
                cn.yapeteam.ymixin.annotations.Mixin targetAnnotation = cn.yapeteam.ymixin.annotations.Mixin.Helper.getAnnotation(source);
                if (targetAnnotation != null)
                    targetClass = targetAnnotation.value();
            } catch (Exception ignored) {
            }
        }
        targetName = targetClass.getName().replace('.', '/');
        Logger.info("Loading mixin {} target class {}", source.name, targetName);
        int try_count = 0;
        while (target == null && try_count < 10) {
            try {
                targetOldBytes = provider.getClassBytes(targetClass);
                target = ASMUtils.node(targetOldBytes);
            } catch (Throwable ignored) {
                try_count++;
                Thread.sleep(500);
            }
        }
        if (target == null)
            Logger.error("Failed to load target class {} for mixin {}", targetClass.getName(), source.name);
        else
            Logger.info("Loaded target class {} for mixin {}, tries: {}", targetClass.getName(), source.name, try_count);
    }
}
