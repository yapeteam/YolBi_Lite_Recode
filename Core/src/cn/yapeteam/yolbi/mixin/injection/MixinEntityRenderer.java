package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.loader.mixin.annotations.*;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Event;
import cn.yapeteam.yolbi.event.impl.player.EventMouseOver;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.impl.combat.Reach;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;

import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Shadow
    private Minecraft mc;

    @Shadow
    private Entity pointedEntity;


    @Inject(
            method = "renderWorldPass", desc = "(IFJ)V",
            target = @Target(
                    value = "INVOKESTATIC",
                    target = "net/minecraft/client/renderer/GlStateManager.disableFog()V",
                    shift = Target.Shift.AFTER
            )
    )
    private void render(@Local(source = "partialTicks", index = 2) float partialTicks) {
        YolBi.instance.getEventManager().post(new EventRender3D(partialTicks));
    }


    @Overwrite(
            method = "getMouseOver", desc = "(F)V"
    )
    public void getMouseOver(@Local(source = "partialTicks", index = 0) float partialTicks)
    {
        Entity entity = this.mc.getRenderViewEntity();

        if (entity != null)
        {
            if (this.mc.theWorld != null)
            {
                this.mc.mcProfiler.startSection("pick");
                this.mc.pointedEntity = null;
                double d0 = (double)this.mc.playerController.getBlockReachDistance();
                this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
                double d1 = d0;
                Vec3 vec3 = entity.getPositionEyes(partialTicks);
                boolean flag = false;
                int i = 3;
                EventMouseOver eventMouseOver = new EventMouseOver(3.0f);
                YolBi.instance.getEventManager().post(eventMouseOver);

                if (this.mc.playerController.extendedReach())
                {
                    d0 = 6.0D;
                    d1 = 6.0D;
                }
                else
                {
                    if (d0 > 3.0D)
                    {
                        flag = true;
                    }
                }

                if (this.mc.objectMouseOver != null)
                {
                    d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3 vec31 = entity.getLook(partialTicks);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                this.pointedEntity = null;
                Vec3 vec33 = null;
                float f = 1.0F;
                List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
                {
                    public boolean apply(Entity p_apply_1_)
                    {
                        return p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;

                for (int j = 0; j < list.size(); ++j)
                {
                    Entity entity1 = (Entity)list.get(j);
                    float f1 = entity1.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
                    MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                    if (axisalignedbb.isVecInside(vec3))
                    {
                        if (d2 >= 0.0D)
                        {
                            this.pointedEntity = entity1;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if (movingobjectposition != null)
                    {
                        double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                        if (d3 < d2 || d2 == 0.0D)
                        {
                            if (entity1 == entity.ridingEntity)
                            {
                                if (d2 == 0.0D)
                                {
                                    this.pointedEntity = entity1;
                                    vec33 = movingobjectposition.hitVec;
                                }
                            }
                            else
                            {
                                this.pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
                if (YolBi.instance != null && YolBi.instance.getModuleManager() != null) {
                    Reach reach = YolBi.instance.getModuleManager().getModule(Reach.class);
                    if (reach != null && reach.isEnabled() && !reach.getValues().isEmpty()) {
                        if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > ((Number) reach.getValues().get(0).getValue()).floatValue()) {
                            this.pointedEntity = null;
                            this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, (EnumFacing) null, new BlockPos(vec33));
                        }
                    }else{
                        if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0D)
                        {
                            this.pointedEntity = null;
                            this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, (EnumFacing)null, new BlockPos(vec33));
                        }
                    }
                }else{
                    if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0D)
                    {
                        this.pointedEntity = null;
                        this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, (EnumFacing)null, new BlockPos(vec33));
                    }
                }


                if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null))
                {
                    this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);

                    if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame)
                    {
                        this.mc.pointedEntity = this.pointedEntity;
                    }
                }

                this.mc.mcProfiler.endSection();
            }
        }
    }

}
