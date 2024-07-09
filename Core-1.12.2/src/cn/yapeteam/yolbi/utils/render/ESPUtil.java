package cn.yapeteam.yolbi.utils.render;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import cn.yapeteam.yolbi.utils.vector.Vector3d;
import cn.yapeteam.yolbi.utils.vector.Vector4d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ESPUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static ConcurrentMap<Entity, Vector4d> finalisedProjections = new ConcurrentHashMap<>();
    public static final ConcurrentMap<Entity, Vector4d> concurrentProjections = new ConcurrentHashMap<>();

    public static ScaledResolution scaledResolution = new ScaledResolution(mc);

    @Listener
    public static void update(EventRender2D event) {
        if (mc.world == null) return;
        final double renderX = ReflectUtil.GetRenderManager$renderPosX(mc.getRenderManager());
        final double renderY = ReflectUtil.GetRenderManager$renderPosY(mc.getRenderManager());
        final double renderZ = ReflectUtil.GetRenderManager$renderPosZ(mc.getRenderManager());
        final int factor = scaledResolution.getScaleFactor();
        final float partialTicks = event.getPartialTicks();

        List<Entity> Entitylist = new ArrayList<>(mc.world.loadedEntityList);

        for (Entity entity : Entitylist)
            if (entity instanceof EntityLivingBase && ESPUtil.isInView(entity) && !(entity == mc.player && mc.gameSettings.thirdPersonView == 0))
                try {
                    final Vector4d position = projectEntity(entity, renderX, renderY, renderZ, partialTicks, factor);
                    if (position != null) {
                        concurrentProjections.put(entity, position);
                    }
                } catch (Exception ignored) {
                    concurrentProjections.remove(entity);
                }
            else concurrentProjections.remove(entity);

        finalisedProjections = new ConcurrentHashMap<>(concurrentProjections);
    }

    private static Vector4d projectEntity(Entity entity, double renderX, double renderY, double renderZ, float partialTicks, int factor) {
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) - renderY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderZ;
        final double width = (entity.width + 0.2) / 2;
        final double height = entity.height + (entity.isSneaking() ? -0.3D : 0.2D) + 0.05;
        final AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
        return projectAABB(aabb, factor);
    }

    private static Frustum frustum;

    public static boolean isInView(Entity ent) {
        if (frustum == null) frustum = new Frustum();
        frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        return frustum.isBoundingBoxInFrustum(ent.getEntityBoundingBox()) || ent.ignoreFrustumCheck;
    }

    private static Vector4d projectAABB(AxisAlignedBB aabb, int factor) {
        final List<Vector3d> vectors = Arrays.asList(
                new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
                new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
                new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
                new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

        Vector4d position = null;
        for (Vector3d vector : vectors) {
            Vector3d projectedVector = project(factor, vector.getX(), vector.getY(), vector.getZ());
            if (projectedVector != null && projectedVector.getZ() >= 0.0D && projectedVector.getZ() < 1.0D) {
                if (position == null) {
                    position = new Vector4d(projectedVector.getX(), projectedVector.getY(), projectedVector.getZ(), 0.0D);
                }

                position = new Vector4d(
                        Math.min(projectedVector.getX(), position.x), Math.min(projectedVector.getY(), position.y),
                        Math.max(projectedVector.getX(), position.z), Math.max(projectedVector.getY(), position.w));
            }
        }
        return position;
    }

    private static Vector3d project(final int factor, final double x, final double y, final double z) {
        FloatBuffer ObjectCoords = ReflectUtil.GetActiveRenderInfo$OBJECTCOORDS();
        if (ObjectCoords == null) return null;
        if (GLU.gluProject((float) x, (float) y, (float) z, ReflectUtil.GetActiveRenderInfo$MODELVIEW(), ReflectUtil.GetActiveRenderInfo$PROJECTION(), ReflectUtil.GetActiveRenderInfo$VIEWPORT(), ReflectUtil.GetActiveRenderInfo$OBJECTCOORDS())) {
            return new Vector3d((ObjectCoords.get(0) / factor), ((Display.getHeight() - ObjectCoords.get(1)) / factor), ObjectCoords.get(2));
        }
        return null;
    }

    public static Vector4d get(Entity entity) {
        if (entity == null) return null;
        return finalisedProjections.get(entity);
    }
}
