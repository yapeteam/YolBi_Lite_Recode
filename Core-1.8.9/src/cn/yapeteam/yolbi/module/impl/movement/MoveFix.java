package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import net.minecraft.util.MathHelper;

public class MoveFix extends Module {
    public MoveFix() {
        super("MoveFix", ModuleCategory.MOVEMENT);
    }

    @Listener
    private void onStrafe(EventStrafe event) {
        if (!RotationManager.active) return;
        if ((event.getForward() * event.getForward() + event.getStrafe() * event.getStrafe()) != 0) {
            float yaw = RotationManager.rotations.x;
            EventStrafe e = yawToStrafe(yaw, direction(event.getYaw()));
            event.setStrafe(e.getStrafe() * 0.98f);
            event.setForward(e.getForward() * 0.98f);
            event.setYaw(yaw);
        }
    }

    public double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public double speed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static float direction(float yaw) {
        float rotationYaw = yaw;

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return rotationYaw;
    }

    public static EventStrafe yawToStrafe(float playerYaw, float moveYaw) {
        int angleDiff = (int) ((MathHelper.wrapAngleTo180_float(moveYaw - playerYaw - 22.5f - 135.0f) + 180.0d) / (45.0d));
        EventStrafe event = new EventStrafe();
        switch (angleDiff) {
            case 0:
                event.setForward(1);
                event.setStrafe(0);
                break;
            case 1:
                event.setForward(1);
                event.setStrafe(-1);
                break;
            case 2:
                event.setForward(0);
                event.setStrafe(-1);
                break;
            case 3:
                event.setForward(-1);
                event.setStrafe(-1);
                break;
            case 4:
                event.setForward(-1);
                event.setStrafe(0);
                break;
            case 5:
                event.setForward(-1);
                event.setStrafe(1);
                break;
            case 6:
                event.setForward(0);
                event.setStrafe(1);
                break;
            case 7:
                event.setForward(1);
                event.setStrafe(1);
                break;

        }
        if (mc.thePlayer.movementInput.sneak) event.slow(0.3d);
        System.out.println(angleDiff + " " + event.getForward() + " " + event.getStrafe());
        return event;
    }
}
