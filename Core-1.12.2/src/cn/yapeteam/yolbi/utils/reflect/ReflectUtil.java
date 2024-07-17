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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.util.math.Vec3d;

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
            EntityRenderer$theShaderGroup, KeyBinding$pressed,
            ShaderGroup$listShaders, Minecraft$timer, Minecraft$leftClickCounter, Minecraft$rightClickDelayTimer,
            EntityPlayerSP$lastReportedYaw, EntityPlayerSP$lastReportedPitch, Entity$motionX, Entity$motionY, Entity$motionZ,
            ActiveRenderInfo$MODELVIEW, ActiveRenderInfo$PROJECTION, ActiveRenderInfo$VIEWPORT, ActiveRenderInfo$OBJECTCOORDS, RenderManager$renderPosX, RenderManager$renderPosY, RenderManager$renderPosZ, Entity$PosX, Entity$PosY, Entity$PosZ,
            SPacketEntityVelocity$motionX, SPacketEntityVelocity$motionY, SPacketEntityVelocity$motionZ, ItemStack$stackTagCompound;


    private static Method
            EntityRenderer$loadShader, EntityRenderer$setupCameraTransform, EntityRenderer$setupOverlayRendering,
            Minecraft$clickMouse, Minecraft$rightClickMouse,
            Entity$getVectorForRotation;

    static {
        try {
            ItemStack$stackTagCompound = ItemStack.class.getDeclaredField(Mapper.map("net/minecraft/item/ItemStack", "stackTagCompound", null, Mapper.Type.Field));
            ItemStack$stackTagCompound.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
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
            Entity$getVectorForRotation = Entity.class.getDeclaredMethod(Mapper.map("net/minecraft/entity/Entity", "getVectorForRotation", "(FF)Lnet/minecraft/util/math/Vec3d;", Mapper.Type.Method), float.class, float.class);
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
            SPacketEntityVelocity$motionX = SPacketEntityVelocity.class.getDeclaredField(Mapper.map("net/minecraft/network/play/server/SPacketEntityVelocity", "motionX", null, Mapper.Type.Field));
            SPacketEntityVelocity$motionX.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            SPacketEntityVelocity$motionY = SPacketEntityVelocity.class.getDeclaredField(Mapper.map("net/minecraft/network/play/server/SPacketEntityVelocity", "motionY", null, Mapper.Type.Field));
            SPacketEntityVelocity$motionY.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }

        try {
            SPacketEntityVelocity$motionZ = SPacketEntityVelocity.class.getDeclaredField(Mapper.map("net/minecraft/network/play/server/SPacketEntityVelocity", "motionZ", null, Mapper.Type.Field));
            SPacketEntityVelocity$motionZ.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
    }

    public static NBTTagCompound ItemStack$getStackTagCompound(ItemStack obj) {
        try {
            return (NBTTagCompound) ItemStack$stackTagCompound.get(obj);
        } catch (IllegalAccessException e) {
            Logger.exception(e);
        }
        return null;
    }

    public static double SPacketEntityVelocity$getMotionX(SPacketEntityVelocity s12PacketEntityVelocity) {
        try {
            return SPacketEntityVelocity$motionX.getDouble(s12PacketEntityVelocity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double SPacketEntityVelocity$getMotionY(SPacketEntityVelocity s12PacketEntityVelocity) {
        try {
            return SPacketEntityVelocity$motionY.getDouble(s12PacketEntityVelocity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static double SPacketEntityVelocity$getMotionZ(SPacketEntityVelocity s12PacketEntityVelocity) {
        try {
            return SPacketEntityVelocity$motionZ.getDouble(s12PacketEntityVelocity);
        } catch (Exception e) {
            Logger.exception(e);
        }
        return 0;
    }

    public static void SPacketEntityVelocity$setMotionX(SPacketEntityVelocity s12PacketEntityVelocity, double value) {
        try {
            SPacketEntityVelocity$motionX.setDouble(s12PacketEntityVelocity, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void SPacketEntityVelocity$setMotionY(SPacketEntityVelocity s12PacketEntityVelocity, double value) {
        try {
            SPacketEntityVelocity$motionY.setDouble(s12PacketEntityVelocity, value);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static void SPacketEntityVelocity$setMotionZ(SPacketEntityVelocity s12PacketEntityVelocity, double value) {
        try {
            SPacketEntityVelocity$motionZ.setDouble(s12PacketEntityVelocity, value);
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

    public static Vec3d Entity$getVectorForRotation(Entity entity, float yaw, float pitch) {
        try {
            return (Vec3d) Entity$getVectorForRotation.invoke(entity, yaw, pitch);
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
