package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@SuppressWarnings("ConstantValue")
@Mixin(Entity.class)
public class MixinEntity extends Entity {
    @Shadow
    public float rotationYaw;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;

    public MixinEntity(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {

    }

    // moveRelative
    @Overwrite(method = "func_191958_b", desc = "(FFFF)V")
    public void moveRelative(float strafe, float up, float forward, float friction) {
        boolean player = ((Entity) this) == Minecraft.getMinecraft().player;
        EventStrafe event = new EventStrafe(strafe, up, forward, friction, rotationYaw);
        if (player) YolBi.instance.getEventManager().post(event);
        if (event.isCancelled()) return;
        strafe = event.getStrafe();
        up = event.getUp();
        forward = event.getForward();
        friction = event.getFriction();
        float f = strafe * strafe + up * up + forward * forward;
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt(f);
            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe *= f;
            up *= f;
            forward *= f;
            float f1 = MathHelper.sin(event.getYaw() * 0.017453292F);
            float f2 = MathHelper.cos(event.getYaw() * 0.017453292F);
            this.motionX += strafe * f2 - forward * f1;
            this.motionY += up;
            this.motionZ += forward * f2 + strafe * f1;
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {

    }
}
