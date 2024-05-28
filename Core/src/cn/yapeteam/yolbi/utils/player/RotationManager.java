package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.loader.utils.vector.Vector3d;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.utils.IMinecraft;
import cn.yapeteam.yolbi.utils.math.MathConst;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.util.vector.Vector2f;

@UtilityClass
public class RotationManager implements IMinecraft {
    private static boolean active;
    public static Vector2f rotations, lastRotations, targetRotations, lastServerRotations;
    private static double rotationSpeed;
    private static MovementFix correctMovement;

    /*
     * This method must be called on Pre Update Event to work correctly
     */
    public static void setRotations(final Vector2f rotations, final double rotationSpeed, final MovementFix correctMovement) {
        RotationManager.targetRotations = rotations;
        RotationManager.rotationSpeed = rotationSpeed * 18;
        RotationManager.correctMovement = correctMovement;
        active = true;

        smooth(rotations, targetRotations, rotationSpeed);
    }

    @Listener
    public static void OnPreUpdate(EventUpdate event) {
        if (!active || rotations == null || lastRotations == null || targetRotations == null || lastServerRotations == null) {
            rotations = lastRotations = targetRotations = lastServerRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        }

        if (active) {
            smooth(lastRotations, targetRotations, rotationSpeed);
        }

        if (correctMovement == MovementFix.BACKWARDS_SPRINT && active) {
            if (Math.abs(rotations.x - Math.toDegrees(PlayerUtil.direction())) > 45) {
                ReflectUtil.SetPressed(mc.gameSettings.keyBindSprint, false);
                mc.thePlayer.setSprinting(false);
            }
        }
    }


    @Listener
    public void onStrafe(EventStrafe event) {
        if (active && (correctMovement == MovementFix.NORMAL || correctMovement == MovementFix.TRADITIONAL) && rotations != null) {
            event.setYaw(rotations.x);
        }
    }

    ;


    @Listener
    public void onPreMotion(EventMotion event) {
        if (active && rotations != null) {
            final float yaw = rotations.x;
            final float pitch = rotations.y;

            event.setYaw(yaw);
            event.setPitch(pitch);

            mc.thePlayer.renderYawOffset = yaw;
            mc.thePlayer.rotationYawHead = yaw;
            mc.thePlayer.rotationPitch = pitch;

            lastServerRotations = new Vector2f(yaw, pitch);

            if (Math.abs((rotations.x - mc.thePlayer.rotationYaw) % 360) < 1 && Math.abs((rotations.y - mc.thePlayer.rotationPitch)) < 1) {
                active = false;

                correctDisabledRotations();
            }

            lastRotations = rotations;
        } else {
            lastRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        }

        targetRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
    }

    private void correctDisabledRotations() {
        final Vector2f rotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        final Vector2f fixedRotations = RotationManager.resetRotation(applySensitivityPatch(rotations));

        mc.thePlayer.rotationYaw = fixedRotations.x;
        mc.thePlayer.rotationPitch = fixedRotations.y;
    }

    public static void smooth(final Vector2f lastRotation, final Vector2f targetRotation, final double speed) {
        float yaw = targetRotation.x;
        float pitch = targetRotation.y;
        final float lastYaw = lastRotation.x;
        final float lastPitch = lastRotation.y;

        if (speed != 0) {
            final float rotationSpeed = (float) speed;

            final double deltaYaw = MathHelper.wrapAngleTo180_float(targetRotation.x - lastRotation.x);
            final double deltaPitch = pitch - lastPitch;

            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            final double distributionYaw = Math.abs(deltaYaw / distance);
            final double distributionPitch = Math.abs(deltaPitch / distance);

            final double maxYaw = rotationSpeed * distributionYaw;
            final double maxPitch = rotationSpeed * distributionPitch;

            final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

            yaw = lastYaw + moveYaw;
            pitch = lastPitch + movePitch;

            for (int i = 1; i <= (int) (Minecraft.getDebugFPS() / 20f + Math.random() * 10); ++i) {

                if (Math.abs(moveYaw) + Math.abs(movePitch) > 1) {
                    yaw += (Math.random() - 0.5) / 1000;
                    pitch -= Math.random() / 200;
                }

                /*
                 * Fixing GCD
                 */
                final Vector2f rotations = new Vector2f(yaw, pitch);
                final Vector2f fixedRotations = applySensitivityPatch(rotations);

                /*
                 * Setting rotations
                 */
                yaw = fixedRotations.x;
                pitch = Math.max(-90, Math.min(90, fixedRotations.y));
            }
        }

        rotations = new Vector2f(yaw, pitch);
    }

    public static double[] getDistance(double x, double z, double y) {
        final double distance = MathHelper.sqrt_double(x * x + z * z), // @off
                yaw = Math.atan2(z, x) * 180.0D / Math.PI - 90.0F,
                pitch = -(Math.atan2(y, distance) * 180.0D / Math.PI); // @on

        return new double[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(
                (float) (yaw - mc.thePlayer.rotationYaw)), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(
                (float) (pitch - mc.thePlayer.rotationPitch))};
    }

    public static double[] getRotationsNeeded(Entity entity) {
        if (entity == null) return null;

        final EntityPlayerSP player = mc.thePlayer;
        final double diffX = entity.posX - player.posX, // @off
                diffY = entity.posY + entity.getEyeHeight() * 0.9 - (player.posY + player.getEyeHeight()),
                diffZ = entity.posZ - player.posZ; // @on

        return getDistance(diffX, diffZ, diffY);
    }

    public Vector2f calculate(final Vector3d from, final Vector3d to) {
        final Vector3d diff = to.subtract(from);
        final double distance = Math.hypot(diff.getX(), diff.getZ());
        final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * MathConst.TO_DEGREES) - 90.0F;
        final float pitch = (float) (-(MathHelper.atan2(diff.getY(), distance) * MathConst.TO_DEGREES));
        return new Vector2f(yaw, pitch);
    }

    public Vector2f calculate(final Vec3 to, final EnumFacing enumFacing) {
        return calculate(new Vector3d(to.xCoord, to.yCoord, to.zCoord), enumFacing);
    }

    private Vector3d getCustomPositionVector(Entity entity) {
        return new Vector3d(entity.posX, entity.posY, entity.posZ);
    }

    private Vector2f getPreviousRotation(EntityPlayerSP playerSP) {
        return new Vector2f(ReflectUtil.GetLastReportedYaw(playerSP), ReflectUtil.GetLastReportedPitch(playerSP));
    }

    public Vector2f calculate(final Vector3d to) {
        return calculate(getCustomPositionVector(mc.thePlayer).add(0, mc.thePlayer.getEyeHeight(), 0), to);
    }

    public Vector2f calculate(final Vector3d position, final EnumFacing enumFacing) {
        double x = position.getX() + 0.5D;
        double y = position.getY() + 0.5D;
        double z = position.getZ() + 0.5D;

        x += (double) enumFacing.getDirectionVec().getX() * 0.5D;
        y += (double) enumFacing.getDirectionVec().getY() * 0.5D;
        z += (double) enumFacing.getDirectionVec().getZ() * 0.5D;
        return calculate(new Vector3d(x, y, z));
    }

    public Vector2f applySensitivityPatch(final Vector2f rotation) {
        final Vector2f previousRotation = getPreviousRotation(mc.thePlayer);
        final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = previousRotation.x + (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
        final float pitch = previousRotation.y + (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
    }

    public Vector2f applySensitivityPatch(final Vector2f rotation, final Vector2f previousRotation) {
        final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = previousRotation.x + (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
        final float pitch = previousRotation.y + (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
    }

    public Vector2f relateToPlayerRotation(final Vector2f rotation) {
        final Vector2f previousRotation = getPreviousRotation(mc.thePlayer);
        final float yaw = previousRotation.x + MathHelper.wrapAngleTo180_float(rotation.x - previousRotation.x);
        final float pitch = MathHelper.clamp_float(rotation.y, -90, 90);
        return new Vector2f(yaw, pitch);
    }

    public Vector2f resetRotation(final Vector2f rotation) {
        if (rotation == null) {
            return null;
        }

        final float yaw = rotation.x + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rotation.x);
        final float pitch = mc.thePlayer.rotationPitch;
        return new Vector2f(yaw, pitch);
    }

}
