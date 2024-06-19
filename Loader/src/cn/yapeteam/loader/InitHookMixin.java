package cn.yapeteam.loader;

import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Target;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class InitHookMixin {
    @Inject(method = "runGameLoop", desc = "()V", target = @Target("HEAD"))
    private void onLoop() {
        BootStrap.initHook();
    }
}
