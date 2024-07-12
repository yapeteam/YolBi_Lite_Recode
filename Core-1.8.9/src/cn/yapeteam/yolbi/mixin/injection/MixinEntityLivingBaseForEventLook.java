package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventLook;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

// for vanilla/mcp
@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBaseForEventLook {
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;

    @Shadow
    protected final Vec3 getVectorForRotation(float pitch, float yaw) {
        return null;
    }

    @Overwrite(
            method = "getLook",
            desc = "(F)Lnet/minecraft/util/Vec3;"
    )
    private Vec3 onLook(float partialTicks) {
        float yaw = this.rotationYaw;
        float pitch = this.rotationPitch;

        EventLook lookEvent = new EventLook(new Vector2f(yaw, pitch));
        YolBi.instance.getEventManager().post(lookEvent);
        yaw = lookEvent.getRotation().x;
        pitch = lookEvent.getRotation().y;

        return this.getVectorForRotation(pitch, yaw);
    }
}
