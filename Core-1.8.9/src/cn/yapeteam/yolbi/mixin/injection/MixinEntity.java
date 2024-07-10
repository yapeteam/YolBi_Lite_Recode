package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.*;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventMove;
import cn.yapeteam.yolbi.event.impl.player.EventPostStrafe;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
@Mixin(Entity.class)
public class MixinEntity extends Entity {
    @Shadow
    public float rotationYaw;

    @Shadow
    public double motionX;

    @Shadow
    public double motionZ;

    public MixinEntity(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {

    }

    @Overwrite(method = "moveFlying", desc = "(FFF)V")
    public void moveFlying(float strafe, float forward, float friction) {
        boolean player = ((Entity) this) == Minecraft.getMinecraft().thePlayer;
        float yaw = this.rotationYaw;

        if (player) {
            final EventStrafe event = new EventStrafe(forward, strafe, friction, rotationYaw);

            YolBi.instance.getEventManager().post(event);

            if (event.isCancelled()) {
                return;
            }

            forward = event.getForward();
            strafe = event.getStrafe();
            friction = event.getFriction();
            yaw = event.getYaw();
        }

        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
            float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);
            this.motionX += strafe * f2 - forward * f1;
            this.motionZ += forward * f2 + strafe * f1;
        }

        if (player) {
            final EventPostStrafe event = new EventPostStrafe();

            YolBi.instance.getEventManager().post(event);
        }
    }

    @Inject(method = "moveEntity", desc = "(DDD)V", target = @Target("HEAD"))
    public void moveEntity(
            @Local(source = "x", index = 1) double x,
            @Local(source = "y", index = 3) double y,
            @Local(source = "z", index = 5) double z
    ) {
        if (((Entity) this) == Minecraft.getMinecraft().thePlayer) {
            final EventMove event = new EventMove(x, y, z);

            YolBi.instance.getEventManager().post(event);

            if (event.isCancelled())
                return;

            x = event.getPosX();
            y = event.getPosY();
            z = event.getPosZ();
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {

    }
}
