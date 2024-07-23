package cn.yapeteam.ymixin.operation.test;

import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Local;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Target;

@Mixin(target.class)
public class source {
    @Inject(method = "func", desc = "()V", target = @Target(value = "INVOKEVIRTUAL", target = "java/io/PrintStream.println(F)V", shift = Target.Shift.BEFORE, ordinal = 1))
    public static void func(@Local(source = "yy", index = 0) float yy, @Local(source = "yy2", index = 1) float yy2) {
        System.out.println(yy + yy2);
    }
}
