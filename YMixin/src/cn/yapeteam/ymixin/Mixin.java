package cn.yapeteam.ymixin;

import cn.yapeteam.ymixin.utils.ASMUtils;
import lombok.Getter;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.util.Objects;

import static cn.yapeteam.ymixin.YMixin.*;

@Getter
public class Mixin {
    private final byte[] targetOldBytes;
    private final ClassNode source;
    private ClassNode target;
    private final String targetName;

    public Mixin(ClassNode source, ClassBytesProvider provider) throws Throwable {
        this.source = source;
        Class<?> targetClass = Objects.requireNonNull(cn.yapeteam.ymixin.annotations.Mixin.Helper.getAnnotation(source)).value();
        targetName = targetClass.getName().replace('.', '/');
        targetOldBytes = provider.getClassBytes(targetClass);
        Logger.info("Loading mixin {}, size: {} bytes", source.name, targetOldBytes.length);
        int tries = 0;
        while (target == null && tries < 5) {
            try {
                target = ASMUtils.node(provider.getClassBytes(targetClass));
            } catch (Throwable ignored) {
                tries++;
                Thread.sleep(500);
            }
        }
        if (target == null)
            Logger.error("Failed to load target class {} for mixin {}", targetClass.getName(), source.name);
        else Logger.info("Loaded target class {} for mixin {}, tries: {}", targetClass.getName(), source.name, tries);
    }
}
