package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.utils.IMinecraft;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Timer;

public class RotationsUtil implements IMinecraft {

    public static float[] getRotationsToPosition(double x, double y, double z) {
        double deltaX = x - mc.thePlayer.posX;
        double deltaY = y - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double deltaZ = z - mc.thePlayer.posZ;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));

        return new float[]{yaw, pitch};
    }

    public static final Timer timer = ReflectUtil.Minecraft$getTimer(mc);

    public static float[] getRotationsToEntity(EntityLivingBase entity, boolean usePartialTicks) {
        if (timer == null) return new float[]{0, 0};
        float partialTicks = timer.renderPartialTicks;

        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks : entity.posX;
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks : entity.posY;
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks : entity.posZ;

        double yDiff = mc.thePlayer.posY - entityY;

        double finalEntityY = yDiff >= 0 ? entityY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entityY;

        return getRotationsToPosition(entityX, finalEntityY, entityZ);
    }

    public static float[] getRotationsToBlockPos(BlockPos pos) {
        return getRotationsToPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public static float getGCD() {
        return (float) (Math.pow(mc.gameSettings.mouseSensitivity * 0.6 + 0.2, 3) * 1.2);
    }

    public static double getRotationDifference(Vector2f curRotations, Vector2f targetRotations) {
        if (curRotations == null || targetRotations == null)
            return 0.0;

        return Math.hypot(getAngleDifference(curRotations.x, targetRotations.x), (curRotations.y - targetRotations.y));
    }

    public static double getRotationDifference(Vector2f rotation) {
        return RotationManager.targetRotations == null ? 0.0 : getRotationDifference(rotation, RotationManager.targetRotations);
    }

    public static float getAngleDifference(float f1, float f2) {
        return ((f1 - f2) % 360f + 540f) % 360f - 180f;
    }
}