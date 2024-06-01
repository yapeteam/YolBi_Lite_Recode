package cn.yapeteam.loader.mixin.operation.test;

import cn.yapeteam.loader.mixin.annotations.Inject;
import cn.yapeteam.loader.mixin.annotations.Local;
import cn.yapeteam.loader.mixin.annotations.Mixin;
import cn.yapeteam.loader.mixin.annotations.Target;

@Mixin(target.class)
public class source {
    @Inject(method = "func", desc = "()V", target = @Target(value = "INVOKEVIRTUAL", target = "java/io/PrintStream.println(F)V", shift = Target.Shift.BEFORE, ordinal = 2))
    public static void func(@Local(source = "yy", index = 0) float yy, @Local(source = "yy2", index = 1) float yy2, @Local(source = "yy3", index = 2) float yy3) {
        System.out.println(yy + yy2 + yy3);
    }
}
