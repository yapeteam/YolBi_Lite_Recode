package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.yolbi.utils.misc.ObjectStore;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
    @Shadow
    public boolean isInvisible() {
        return false;
    }

    @Overwrite(
            method = "isInvisibleToPlayer",
            desc = "(Lnet/minecraft/entity/player/EntityPlayer;)Z"
    )
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        Object value = ObjectStore.objects.get("AntiInvisible");
        if (value != null) return !(boolean) value;
        return isInvisible();
    }
}
