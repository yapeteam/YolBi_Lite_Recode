package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.player.*;
import cn.yapeteam.yolbi.event.impl.render.EventRotationsRender;
import cn.yapeteam.yolbi.utils.IMinecraft;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import cn.yapeteam.yolbi.utils.vector.Vector3d;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@UtilityClass
public class RotationManager implements IMinecraft {
    public static boolean active;
    public static Vector2f rotations, lastRotations, targetRotations, lastServerRotations;
    private static double rotationSpeed;

    public float renderPitchHead;

    public float prevRenderPitchHead;

    /*
     * This method must be called on Pre Update Event to work correctly
     */
    public static void setRotations(final Vector2f rotations, final double rotationSpeed) {
        RotationManager.targetRotations = rotations;
        RotationManager.rotationSpeed = rotationSpeed * 18;
        active = true;
        smooth(rotations, targetRotations, rotationSpeed);
    }

    @Listener
    public static void onPreUpdate(EventUpdate event) {
        if (!active || rotations == null || lastRotations == null || targetRotations == null || lastServerRotations == null) {
            rotations = lastRotations = targetRotations = lastServerRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        }

        if (active)
            smooth(lastRotations, targetRotations, rotationSpeed);

        //backward sprint fix
        if (active) {
            if (Math.abs(rotations.x - Math.toDegrees(PlayerUtil.direction())) > 45) {
                ReflectUtil.SetPressed(mc.gameSettings.keyBindSprint, false);
                mc.thePlayer.setSprinting(false);
            }
        }
    }

    @Listener(Priority.LOWER)
    public void onRender(EventRotationsRender event) {
        if (active && rotations != null) {
            event.setYaw(rotations.x);
            event.setPitch(rotations.y);
        }
    }

    @Listener
    private void onJump(EventJump event) {
        if (active && rotations != null)
            event.setYaw(rotations.x);
    }

    @Listener
    private void onLook(EventLook event) {
        if (active && rotations != null)
            event.setRotation(rotations);
    }

    @Listener
    public void onPreMotion(EventMotion event) {
        if (active && rotations != null) {
            final float yaw = rotations.x;
            final float pitch = rotations.y;

            event.setYaw(yaw);
            event.setPitch(pitch);

            mc.thePlayer.renderYawOffset = yaw;
            mc.thePlayer.rotationYawHead = yaw;
            //todo: fix this
            renderPitchHead = pitch;

            lastServerRotations = new Vector2f(yaw, pitch);

            if (Math.abs((rotations.x - mc.thePlayer.rotationYaw) % 360) < 1 && Math.abs((rotations.y - mc.thePlayer.rotationPitch)) < 1)
                stop();

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

    public static void smooth() {
        smooth(lastRotations, targetRotations, rotationSpeed);
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

            final Vector2f fixedRotations = applySensitivityPatch(new Vector2f(yaw, pitch));

            /*
             * Setting rotations
             */
            yaw = fixedRotations.x;
            pitch = Math.max(-90, Math.min(90, fixedRotations.y));
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
        final double diffX = diff.getX();
        final double diffY = diff.getY();
        final double diffZ = diff.getZ();
        float yaw = (float) (from.getX() + MathHelper.wrapAngleTo180_float((float) ((float) (Math.atan2(diffZ, diffX) * 57.295780181884766) - 90.0f - from.getX())));
        float pitch = clamp((float) (from.getY() + MathHelper.wrapAngleTo180_float((float) ((float) (-(Math.atan2(diffY, MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ)) * 57.295780181884766)) - from.getY()))));
        return new Vector2f(yaw, pitch);
    }

    public static float clamp(final float n) {
        return MathHelper.clamp_float(n, -90.0f, 90.0f);
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

    public Vector2f calculate(final Entity entity) {
        return calculate(getCustomPositionVector(entity).add(0, Math.max(0, Math.min(mc.thePlayer.posY - entity.posY +
                mc.thePlayer.getEyeHeight(), (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.9)), 0));
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
        if (rotation == null)
            return null;
        final float yaw = rotation.x + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rotation.x);
        final float pitch = mc.thePlayer.rotationPitch;
        return new Vector2f(yaw, pitch);
    }

    public static void reset() {
        setRotations(new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch), 100);
        smooth();
    }

    public static void stop() {
        active = false;
        correctDisabledRotations();
    }
}
