package cn.yapeteam.ymixin.annotations;

import cn.yapeteam.ymixin.utils.ASMUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm_9_2.tree.AnnotationNode;
import org.objectweb.asm_9_2.tree.MethodNode;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface Redirect {
    String method();

    String desc();

    Target target();

    class Helper {
        public static Redirect fromNode(AnnotationNode annotation) {
            return new Redirect() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Redirect.class;
                }

                @Override
                public String method() {
                    return ASMUtils.getAnnotationValue(annotation, "method");
                }

                @Override
                public String desc() {
                    return ASMUtils.getAnnotationValue(annotation, "desc");
                }

                @Override
                public Target target() {
                    AnnotationNode annotationNode = ASMUtils.getAnnotationValue(annotation, "target");
                    if (annotationNode == null) return null;
                    return Target.Helper.fromNode(annotationNode);
                }
            };
        }

        public static boolean isAnnotation(@NotNull AnnotationNode node) {
            return node.desc.contains(ASMUtils.slash(Overwrite.class.getName()));
        }

        public static boolean hasAnnotation(@NotNull MethodNode node) {
            return node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(Overwrite.Helper::isAnnotation);
        }

        public static @Nullable Redirect getAnnotation(MethodNode node) {
            if (!hasAnnotation(node)) return null;
            return fromNode(node.visibleAnnotations.stream().filter(Redirect.Helper::isAnnotation).findFirst().orElse(null));
        }
    }
}
