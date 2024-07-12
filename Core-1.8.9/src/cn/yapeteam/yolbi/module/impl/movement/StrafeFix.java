package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class StrafeFix extends Module {
    private final BooleanValue allowSprint = new BooleanValue("Allow Sprint", false);

    public StrafeFix() {
        super("StrafeFix", ModuleCategory.MOVEMENT);
        this.addValues(allowSprint);
    }

    @Listener
    private void onStrafe(EventStrafe event) {
        if (!RotationManager.active) return;
        if (!allowSprint.getValue())
            mc.thePlayer.setSprinting(false);
        runStrafeFixLoop(event);
    }

    public void runStrafeFixLoop(EventStrafe event) {
        if (event.isCancelled())
            return;

        float yaw = RotationManager.rotations.x;
        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();
        float factor = strafe * strafe + forward * forward;

        if (factor >= 1.0E-4F) {
            factor = MathHelper.sqrt_float(factor);
            if (factor < 1.0F) {
                factor = 1.0F;
            }

            factor = friction / factor;
            strafe *= factor;
            forward *= factor;

            float yawSin = MathHelper.sin((yaw * (float) Math.PI / 180F));
            float yawCos = MathHelper.cos((yaw * (float) Math.PI / 180F));

            mc.thePlayer.motionX += strafe * yawCos - forward * yawSin;
            mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin;
        }

        event.setCancelled(true);
    }

    public void applyStrafeToPlayer(EventStrafe event) {
        float yaw = RotationManager.rotations.x;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int dif = (int) ((MathHelper.wrapAngleTo180_float(player.rotationYaw - yaw - 23.5f - 135) + 180) / 45);

        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();

        float calcForward = 0f;
        float calcStrafe = 0f;

        switch (dif) {
            case 0:
                calcForward = forward;
                calcStrafe = strafe;
                break;
            case 1:
                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
                break;
            case 2:
                calcForward = strafe;
                calcStrafe = -forward;
                break;
            case 3:
                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
                break;
            case 4:
                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            case 5:
                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
                break;
            case 6:
                calcForward = -strafe;
                calcStrafe = forward;
                break;
            case 7:
                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
                break;
        }

        if (calcForward > 1f || (calcForward < 0.9f && calcForward > 0.3f) || calcForward < -1f || (calcForward > -0.9f && calcForward < -0.3f)) {
            calcForward *= 0.5f;
        }

        if (calcStrafe > 1f || (calcStrafe < 0.9f && calcStrafe > 0.3f) || calcStrafe < -1f || (calcStrafe > -0.9f && calcStrafe < -0.3f)) {
            calcStrafe *= 0.5f;
        }

        float d = calcStrafe * calcStrafe + calcForward * calcForward;

        if (d >= 1.0E-4f) {
            d = MathHelper.sqrt_float(d);
            if (d < 1.0f) d = 1.0f;
            d = friction / d;
            calcStrafe *= d;
            calcForward *= d;
            float yawSin = MathHelper.sin((yaw * (float) Math.PI / 180f));
            float yawCos = MathHelper.cos((yaw * (float) Math.PI / 180f));
            player.motionX += calcStrafe * yawCos - calcForward * yawSin;
            player.motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }
    }
}
