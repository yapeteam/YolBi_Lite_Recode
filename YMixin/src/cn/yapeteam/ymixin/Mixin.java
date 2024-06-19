package cn.yapeteam.ymixin;

import cn.yapeteam.ymixin.utils.ASMUtils;
import lombok.Getter;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.util.Objects;

import static cn.yapeteam.ymixin.YMixin.*;

@Getter
public class Mixin {
    private byte[] targetOldBytes = null;
    private final ClassNode source;
    private ClassNode target;
    private final String targetName;

    public Mixin(ClassNode source, ClassBytesProvider provider) throws Throwable {
        this.source = source;
        Class<?> targetClass = Objects.requireNonNull(cn.yapeteam.ymixin.annotations.Mixin.Helper.getAnnotation(source)).value();
        targetName = targetClass.getName().replace('.', '/');
        Logger.info("Loading mixin {}", source.name);
        int byte_tries = 0;
        while (targetOldBytes == null && byte_tries < 5) {
            try {
                targetOldBytes = provider.getClassBytes(targetClass);
            } catch (Throwable ignored) {
                byte_tries++;
                Thread.sleep(500);
            }
        }
        int node_tries = 0;
        while (target == null && node_tries < 5) {
            try {
                target = ASMUtils.node(targetOldBytes);
            } catch (Throwable ignored) {
                node_tries++;
                Thread.sleep(500);
            }
        }
        if (target == null)
            Logger.error("Failed to load target class {} for mixin {}", targetClass.getName(), source.name);
        else
            Logger.info("Loaded target class {} for mixin {}, tries: {}+{}", targetClass.getName(), source.name, byte_tries, node_tries);
    }
}
