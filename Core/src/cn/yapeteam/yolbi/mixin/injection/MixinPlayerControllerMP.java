package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.*;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.type.CancellableEvent;
import cn.yapeteam.yolbi.module.impl.combat.Reach;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldSettings;

/**
 * @author yuxiangll
 * @since 2024/1/7 20:58
 * IntelliJ IDEA
 */
@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Shadow
    private WorldSettings.GameType currentGameType = WorldSettings.GameType.SURVIVAL;

    @Inject(
            method = "attackEntity",
            desc = "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V",
            target = @Target("HEAD")
    )
    public void attackEntity(@Local(source = "targetEntity", index = 2) Entity targetEntity) {
        if (targetEntity != null && ((CancellableEvent) YolBi.instance.getEventManager().post(new EventAttack(targetEntity))).isCancelled()) {
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    @Overwrite(
            method = "getBlockReachDistance",
            desc = "()F"
    )
    public float getBlockReachDistance() {
        if (YolBi.instance != null && YolBi.instance.getModuleManager() != null) {
            Reach reach = YolBi.instance.getModuleManager().getModule(Reach.class);
            if (reach != null && reach.isEnabled() && !reach.getValues().isEmpty())
                return ((Number) reach.getValues().get(0).getValue()).floatValue();
        }
        return this.currentGameType.isCreative() ? 5.0F : 4.0F;
    }
}
