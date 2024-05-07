package cn.yapeteam.yolbi.mixin.injection;

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

    @Inject(method = "getMouseOver", desc = "(F)V", target = @Target(value = "INVOKEVIRTUAL", target = "net/minecraft/client/multiplayer/PlayerControllerMP.extendedReach()Z", shift = Target.Shift.BEFORE))
    private void onPreMouseOver(@Local(source = "partialTicks", index = 1) float partialTicks) {
        YolBi.instance.getEventManager().post(new EventMouseOver(partialTicks));
    }

    @Inject(method = "getMouseOver", desc = "(F)V", target = @Target(value = "INVOKEVIRTUAL", target = "net/minecraft/profiler/Profiler.endSection()V", shift = Target.Shift.AFTER))
    private void onPostMouseOver(
            @Local(source = "vec3", index = 7) Vec3 vec3,
            @Local(source = "vec33", index = 12) Vec3 vec33,
            @Local(source = "flag", index = 8) boolean flag
    ) {
        if (YolBi.instance != null && YolBi.instance.getModuleManager() != null) {
            Reach reach = YolBi.instance.getModuleManager().getModule(Reach.class);
            if (reach != null && reach.isEnabled() && !reach.getValues().isEmpty())
                if (flag && vec33 != null && vec3 != null && vec3.distanceTo(vec33) > ((Number) reach.getValues().get(0).getValue()).floatValue()) {
                    this.pointedEntity = null;
                    this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
                }
        }
    }
}
