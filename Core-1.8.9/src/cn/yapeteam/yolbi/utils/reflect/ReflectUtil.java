package cn.yapeteam.yolbi.utils.reflect;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.ymixin.utils.Mapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class ReflectUtil {
    private static Field
            EntityRenderer$theShaderGroup;
    private static Field KeyBinding$pressed;
    private static Field ShaderGroup$listShaders;
    private static Field Minecraft$timer;
    private static Field Minecraft$leftClickCounter;
    public static Field Minecraft$rightClickDelayTimer;
    private static Field EntityPlayerSP$lastReportedYaw;
    private static Field EntityPlayerSP$lastReportedPitch;
    private static Field Entity$motionX;
    private static Field Entity$motionY;
    private static Field Entity$motionZ;
    private static Field ActiveRenderInfo$MODELVIEW;
    private static Field ActiveRenderInfo$PROJECTION;
    private static Field ActiveRenderInfo$VIEWPORT;
    private static Field ActiveRenderInfo$OBJECTCOORDS;
    private static Field RenderManager$renderPosX;
    private static Field RenderManager$renderPosY;
    private static Field RenderManager$renderPosZ;
    private static Field Entity$PosX;
    private static Field Entity$PosY;
    private static Field Entity$PosZ;
    private static Field S12PacketEntityVelocity$motionX;
    private static Field S12PacketEntityVelocity$motionY;
    private static Field S12PacketEntityVelocity$motionZ;
    private static Field C07PacketPlayerDigging$position;
    private static Field C07PacketPlayerDigging$facing;
    private static Field C03PacketPlayer$pitch;


    private static Method
            EntityRenderer$loadShader, EntityRenderer$setupCameraTransform, EntityRenderer$setupOverlayRendering,
            Minecraft$clickMouse, Minecraft$rightClickMouse,
            Entity$getVectorForRotation;

    static {
        try {
            Minecraft$clickMouse = Minecraft.class.getDeclaredMethod(Mapper.map("net/minecraft/client/Minecraft", "clickMouse", "()V", Mapper.Type.Method));
            Minecraft$clickMouse.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Logger.exception(e);
        }
        try {
            Minecraft$rightClickMouse = Minecraft.class.getDeclaredMethod(Mapper.map("net/minecraft/client/Minecraft", "rightClickMouse", "()V", Mapper.Type.Method));
            Minecraft$rightClickMouse.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Logger.exception(e);
        }
        try {
            Entity$getVectorForRotation = Entity.class.getDeclaredMethod(Mapper.map("net/minecraft/entity/Entity", "getVectorForRotation", "(FF)Lnet/minecraft/util/Vec3;", Mapper.Type.Method), float.class, float.class);
            Entity$getVectorForRotation.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Logger.exception(e);
        }
        try {
            EntityRenderer$theShaderGroup = EntityRenderer.class.getDeclaredField(Mapper.map("net/minecraft/client/renderer/EntityRenderer", "theShaderGroup", null, Mapper.Type.Field));
            EntityRenderer$loadShader = EntityRenderer.class.getDeclaredMethod(Mapper.map("net/minecraft/client/renderer/EntityRenderer", "loadShader", null, Mapper.Type.Method), ResourceLocation.class);
            EntityRenderer$theShaderGroup.setAccessible(true);
            EntityRenderer$loadShader.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            Logger.exception(e);
        }
        try {
            KeyBinding$pressed = KeyBinding.class.getDeclaredField(Mapper.map("net/minecraft/client/settings/KeyBinding", "pressed", null, Mapper.Type.Field));
            KeyBinding$pressed.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            ShaderGroup$listShaders = ShaderGroup.class.getDeclaredField(Mapper.map("net/minecraft/client/shader/ShaderGroup", "listShaders", null, Mapper.Type.Field));
            ShaderGroup$listShaders.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            Minecraft$timer = Minecraft.class.getDeclaredField(Mapper.map("net/minecraft/client/Minecraft", "timer", null, Mapper.Type.Field));
            Minecraft$timer.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            Minecraft$leftClickCounter = Minecraft.class.getDeclaredField(Mapper.map("net/minecraft/client/Minecraft", "leftClickCounter", null, Mapper.Type.Field));
            Minecraft$leftClickCounter.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            Minecraft$rightClickDelayTimer = Minecraft.class.getDeclaredField(Mapper.map("net/minecraft/client/Minecraft", "rightClickDelayTimer", null, Mapper.Type.Field));
            Minecraft$rightClickDelayTimer.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            EntityPlayerSP$lastReportedYaw = EntityPlayerSP.class.getDeclaredField(Mapper.map("net/minecraft/client/entity/EntityPlayerSP", "lastReportedYaw", null, Mapper.Type.Field));
            EntityPlayerSP$lastReportedYaw.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            EntityPlayerSP$lastReportedPitch = EntityPlayerSP.class.getDeclaredField(Mapper.map("net/minecraft/client/entity/EntityPlayerSP", "lastReportedPitch", null, Mapper.Type.Field));
            EntityPlayerSP$lastReportedPitch.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            EntityRenderer$setupCameraTransform = EntityRenderer.class.getDeclaredMethod(Mapper.map("net/minecraft/client/renderer/EntityRenderer", "setupCameraTransform", "(FI)V", Mapper.Type.Method), float.class, int.class);
            EntityRenderer$setupCameraTransform.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Logger.exception(e);
        }
        try {
            EntityRenderer$setupOverlayRendering = EntityRenderer.class.getDeclaredMethod(Mapper.map("net/minecraft/client/renderer/EntityRenderer", "setupOverlayRendering", "()V", Mapper.Type.Method));
            EntityRenderer$setupOverlayRendering.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Logger.exception(e);
        }
        try {
            ActiveRenderInfo$MODELVIEW = ActiveRenderInfo.class.getDeclaredField(Mapper.map("net/minecraft/client/renderer/ActiveRenderInfo", "MODELVIEW", null, Mapper.Type.Field));
            ActiveRenderInfo$PROJECTION = ActiveRenderInfo.class.getDeclaredField(Mapper.map("net/minecraft/client/renderer/ActiveRenderInfo", "PROJECTION", null, Mapper.Type.Field));
            ActiveRenderInfo$VIEWPORT = ActiveRenderInfo.class.getDeclaredField(Mapper.map("net/minecraft/client/renderer/ActiveRenderInfo", "VIEWPORT", null, Mapper.Type.Field));
            ActiveRenderInfo$OBJECTCOORDS = ActiveRenderInfo.class.getDeclaredField(Mapper.map("net/minecraft/client/renderer/ActiveRenderInfo", "OBJECTCOORDS", null, Mapper.Type.Field));
            ActiveRenderInfo$MODELVIEW.setAccessible(true);
            ActiveRenderInfo$PROJECTION.setAccessible(true);
            ActiveRenderInfo$VIEWPORT.setAccessible(true);
            ActiveRenderInfo$OBJECTCOORDS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            RenderManager$renderPosX = RenderManager.class.getDeclaredField(Mapper.map("net/minecraft/client/renderer/entity/RenderManager", "renderPosX", null, Mapper.Type.Field));
            RenderManager$renderPosY = RenderManager.class.getDeclaredField(Mapper.map("net/minecraft/client/renderer/entity/RenderManager", "renderPosY", null, Mapper.Type.Field));
            RenderManager$renderPosZ = RenderManager.class.getDeclaredField(Mapper.map("net/minecraft/client/renderer/entity/RenderManager", "renderPosZ", null, Mapper.Type.Field));
            RenderManager$renderPosX.setAccessible(true);
            RenderManager$renderPosY.setAccessible(true);
            RenderManager$renderPosZ.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            Entity$motionX = Entity.class.getDeclaredField(Mapper.map("net/minecraft/entity/Entity", "motionX", null, Mapper.Type.Field));
            Entity$motionX.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            Entity$motionY = Entity.class.getDeclaredField(Mapper.map("net/minecraft/entity/Entity", "motionY", null, Mapper.Type.Field));
            Entity$motionY.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            Entity$motionZ = Entity.class.getDeclaredField(Mapper.map("net/minecraft/entity/Entity", "motionZ", null, Mapper.Type.Field));
            Entity$motionZ.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            Entity$PosX = Entity.class.getDeclaredField(Mapper.map("net/minecraft/entity/Entity", "posX", null, Mapper.Type.Field));
            Entity$PosX.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            Entity$PosY = Entity.class.getDeclaredField(Mapper.map("net/minecraft/entity/Entity", "posY", null, Mapper.Type.Field));
            Entity$PosY.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            Entity$PosZ = Entity.class.getDeclaredField(Mapper.map("net/minecraft/entity/Entity", "posZ", null, Mapper.Type.Field));
            Entity$PosZ.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            S12PacketEntityVelocity$motionX = S12PacketEntityVelocity.class.getDeclaredField(Mapper.map("net/minecraft/network/play/server/S12PacketEntityVelocity", "motionX", null, Mapper.Type.Field));
            S12PacketEntityVelocity$motionX.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            S12PacketEntityVelocity$motionY = S12PacketEntityVelocity.class.getDeclaredField(Mapper.map("net/minecraft/network/play/server/S12PacketEntityVelocity", "motionY", null, Mapper.Type.Field));
            S12PacketEntityVelocity$motionY.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            S12PacketEntityVelocity$motionZ = S12PacketEntityVelocity.class.getDeclaredField(Mapper.map("net/minecraft/network/play/server/S12PacketEntityVelocity", "motionZ", null, Mapper.Type.Field));
            S12PacketEntityVelocity$motionZ.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            C07PacketPlayerDigging$position = C07PacketPlayerDigging.class.getDeclaredField(Mapper.map("net/minecraft/network/play/client/C07PacketPlayerDigging", "position", null, Mapper.Type.Field));
            C07PacketPlayerDigging$position.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            C07PacketPlayerDigging$facing = C07PacketPlayerDigging.class.getDeclaredField(Mapper.map("net/minecraft/network/play/client/C07PacketPlayerDigging", "facing", null, Mapper.Type.Field));
            C07PacketPlayerDigging$facing.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        try {
            C03PacketPlayer$pitch = C03PacketPlayer.class.getDeclaredField(Mapper.map("net/minecraft/network/play/client/C03PacketPlayer", "pitch", null, Mapper.Type.Field));
            C03PacketPlayer$pitch.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
    }

    public static void C07PacketPlayerDigging$setPosition(C07PacketPlayerDigging obj, BlockPos position) {
        try {
            C07PacketPlayerDigging$position.set(obj, position);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void C07PacketPlayerDigging$setFacing(C07PacketPlayerDigging obj, EnumFacing facing) {
        try {
            C07PacketPlayerDigging$facing.set(obj, facing);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void C03PacketPlayer$setPitch(C03PacketPlayer obj, float pitch) {
        try {
            C07PacketPlayerDigging$facing.setFloat(obj, pitch);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static double S12PacketEntityVelocity$getMotionX(S12PacketEntityVelocity s12PacketEntityVelocity) {
        try {
            return S12PacketEntityVelocity$motionX.getDouble(s12PacketEntityVelocity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double S12PacketEntityVelocity$getMotionY(S12PacketEntityVelocity s12PacketEntityVelocity) {
        try {
            return S12PacketEntityVelocity$motionY.getDouble(s12PacketEntityVelocity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double S12PacketEntityVelocity$getMotionZ(S12PacketEntityVelocity s12PacketEntityVelocity) {
        try {
            return S12PacketEntityVelocity$motionZ.getDouble(s12PacketEntityVelocity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static void S12PacketEntityVelocity$setMotionX(S12PacketEntityVelocity s12PacketEntityVelocity, double value) {
        try {
            S12PacketEntityVelocity$motionX.setDouble(s12PacketEntityVelocity, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void S12PacketEntityVelocity$setMotionY(S12PacketEntityVelocity s12PacketEntityVelocity, double value) {
        try {
            S12PacketEntityVelocity$motionY.setDouble(s12PacketEntityVelocity, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void S12PacketEntityVelocity$setMotionZ(S12PacketEntityVelocity s12PacketEntityVelocity, double value) {
        try {
            S12PacketEntityVelocity$motionZ.setDouble(s12PacketEntityVelocity, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static double Entity$getPosX(Entity entity) {
        try {
            return Entity$PosX.getDouble(entity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double Entity$getPosY(Entity entity) {
        try {
            return Entity$PosY.getDouble(entity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double Entity$getPosZ(Entity entity) {
        try {
            return Entity$PosZ.getDouble(entity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double Entity$getMotionX(Entity entity) {
        try {
            return Entity$motionX.getDouble(entity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double Entity$getMotionY(Entity entity) {
        try {
            return Entity$motionY.getDouble(entity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double Entity$getMotionZ(Entity entity) {
        try {
            return Entity$motionZ.getDouble(entity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }


    public static void Entity$setMotionX(Entity entity, double value) {
        try {
            Entity$motionX.setDouble(entity, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void Entity$setMotionY(Entity entity, double value) {
        try {
            Entity$motionY.setDouble(entity, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void Entity$setMotionZ(Entity entity, double value) {
        try {
            Entity$motionZ.setDouble(entity, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }


    public static void Minecraft$clickMouse(Minecraft minecraft) {
        try {
            Minecraft$clickMouse.invoke(minecraft);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void Minecraft$rightClickMouse(Minecraft minecraft) {
        try {
            Minecraft$rightClickMouse.invoke(minecraft);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static Timer Minecraft$getTimer(Minecraft minecraft) {
        try {
            return (Timer) Minecraft$timer.get(minecraft);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    public static ShaderGroup GetEntityRenderer$theShaderGroup(EntityRenderer entityRenderer) {
        try {
            return (ShaderGroup) EntityRenderer$theShaderGroup.get(entityRenderer);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    public static FloatBuffer GetActiveRenderInfo$MODELVIEW() {
        try {
            return (FloatBuffer) ActiveRenderInfo$MODELVIEW.get(null);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    public static FloatBuffer GetActiveRenderInfo$PROJECTION() {
        try {
            return (FloatBuffer) ActiveRenderInfo$PROJECTION.get(null);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    public static IntBuffer GetActiveRenderInfo$VIEWPORT() {
        try {
            return (IntBuffer) ActiveRenderInfo$VIEWPORT.get(null);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    public static FloatBuffer GetActiveRenderInfo$OBJECTCOORDS() {
        try {
            return (FloatBuffer) ActiveRenderInfo$OBJECTCOORDS.get(null);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    public static double GetRenderManager$renderPosX(RenderManager renderManager) {
        try {
            return RenderManager$renderPosX.getDouble(renderManager);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double GetRenderManager$renderPosY(RenderManager renderManager) {
        try {
            return RenderManager$renderPosY.getDouble(renderManager);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double GetRenderManager$renderPosZ(RenderManager renderManager) {
        try {
            return RenderManager$renderPosZ.getDouble(renderManager);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static void SetEntityRenderer$theShaderGroup(EntityRenderer entityRenderer, ShaderGroup theShaderGroup) {
        try {
            EntityRenderer$theShaderGroup.set(entityRenderer, theShaderGroup);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }


    public static void EntityRenderer$loadShader(EntityRenderer entityRenderer, ResourceLocation resourceLocationIn) {
        try {
            EntityRenderer$loadShader.invoke(entityRenderer, resourceLocationIn);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void EntityRenderer$setupCameraTransform(EntityRenderer entityRenderer, float partialTicks,
                                                           int pass) {
        try {
            EntityRenderer$setupCameraTransform.invoke(entityRenderer, partialTicks, pass);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void EntityRenderer$setupOverlayRendering(EntityRenderer entityRenderer) {
        try {
            EntityRenderer$setupOverlayRendering.invoke(entityRenderer);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static Vec3 Entity$getVectorForRotation(Entity entity, float yaw, float pitch) {
        try {
            return (Vec3) Entity$getVectorForRotation.invoke(entity, yaw, pitch);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    public static List<Shader> GetShaderGroup$listShaders(ShaderGroup shaderGroup) {
        try {
            return (List<Shader>) ShaderGroup$listShaders.get(shaderGroup);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return null;
    }

    public static void SetShaderGroup$listShaders(ShaderGroup shaderGroup, List<Shader> listShaders) {
        try {
            ShaderGroup$listShaders.set(shaderGroup, listShaders);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static int GetLeftClickCounter(Minecraft mc) {
        try {
            return Minecraft$leftClickCounter.getInt(mc);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static void SetLeftClickCounter(Minecraft mc, int value) {
        try {
            Minecraft$leftClickCounter.setInt(mc, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static int GetRightClickDelayTimer(Minecraft mc) {
        try {
            return Minecraft$rightClickDelayTimer.getInt(mc);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static void SetRightClickDelayTimer(Minecraft mc, int value) {
        try {
            Minecraft$rightClickDelayTimer.setInt(mc, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static boolean IsPressed(KeyBinding keyBinding) {
        try {
            return KeyBinding$pressed.getBoolean(keyBinding);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return false;
    }


    public static void SetPressed(KeyBinding keyBinding, boolean value) {
        try {
            KeyBinding$pressed.setBoolean(keyBinding, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static float GetLastReportedYaw(EntityPlayerSP player) {
        try {
            return EntityPlayerSP$lastReportedYaw.getFloat(player);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static void setLastReportedYaw(EntityPlayerSP player, float value) {
        try {
            EntityPlayerSP$lastReportedYaw.setFloat(player, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static float GetLastReportedPitch(EntityPlayerSP player) {
        try {
            return EntityPlayerSP$lastReportedPitch.getFloat(player);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static void setLastReportedPitch(EntityPlayerSP player, float value) {
        try {
            EntityPlayerSP$lastReportedPitch.setFloat(player, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static final boolean hasOptifine = Arrays.stream(GameSettings.class.getFields()).anyMatch(f -> f.getName().equals("ofFastRender"));

    public static Field getField(Class<?> clz, String name) {
        try {
            Field field = clz.getDeclaredField(Mapper.mapFieldWithSuper(clz.getName(), name, null));
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
        return null;
    }

    public static <T> T getField(Object obj, String name) {
        try {
            return (T) Objects.requireNonNull(getField(obj.getClass(), name)).get(obj);
        } catch (IllegalAccessException e) {
            Logger.exception(e);
        }
        return null;
    }

    public static <T> T getField(Field field, Object obj) {
        try {
            Object o = field.get(obj);
            if (o != null) return (T) o;
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
        throw new RuntimeException();
    }
}
