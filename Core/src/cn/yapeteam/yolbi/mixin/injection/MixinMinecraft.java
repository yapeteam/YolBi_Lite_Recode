package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Target;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "runTick", desc = "()V",target = @Target("HEAD"))
    public void onTick() {
        YolBi.instance.getEventManager().post(new EventTick());
    }
}
