package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.mixin.annotations.*;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventMouseOver;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.impl.combat.Reach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    private Minecraft mc;
    @Shadow
    private Entity pointedEntity;

    @Inject(
            method = "renderWorldPass", desc = "(IFJ)V",
            target = @Target(
                    value = "INVOKESTATIC",
                    target = "net/minecraft/client/renderer/GlStateManager.disableFog()V",
                    shift = Target.Shift.AFTER
            )
    )
    private void render(@Local(source = "partialTicks", index = 2) float partialTicks) {
        YolBi.instance.getEventManager().post(new EventRender3D(partialTicks));
    }

    @Inject(
            method = "getMouseOver",
            desc = "(F)V",
            target = @Target(value = "ISTORE",
                    shift = Target.Shift.AFTER))
    private void getMouseOver(@Local(source = "partialTicks", index = 1) float partialTicks) {
        YolBi.instance.getEventManager().post(new EventMouseOver(3.0f));
    }

    @Modify(method = "getMouseOver", desc = "(F)V")
    private void modifygetMouseOver(@Local(source = "partialTicks", index = 1) float partialTicks) {

    }
}