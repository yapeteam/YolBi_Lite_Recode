package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.loader.mixin.annotations.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.Timer;
import net.minecraft.world.World;

@Mixin(ModelBiped.class)
public class MixinModelBiped extends ModelBiped{

    public MixinModelBiped(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
        super();
    }

    @Shadow
    public float prevRenderPitchHead;

    @Shadow
    public float renderPitchHead;

    /*

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        this.bipedHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        this.bipedHead.rotateAngleX = headPitch / (180F / (float)Math.PI);

        final EntityPlayerSP entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityIn == entityPlayer) {
            this.bipedHead.rotateAngleX = (entityPlayer.prevRenderPitchHead + (entityPlayer.renderPitchHead - entityPlayer.prevRenderPitchHead) * Minecraft.getMinecraft().timer.renderPartialTicks) / (180.0F / (float) Math.PI);
        }

        this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.bipedRightArm.rotateAngleZ = 0.0F;

     */

    @Inject(
            method = "setRotationAngles", desc = "(FFFFFFLnet/minecraft/entity/Entity;)V",
            target = @Target(
                    value = "INVOKESTATIC",
                    target = "net/minecraft/util/MathHelper.cos(F)F",
                    shift = Target.Shift.BEFORE
            )
    )
    public void setRotationAngles(@Local(source = "EntityIn",index = 7)Entity entityIn) {
//        System.out.println("setRotationAngles");
        final EntityPlayerSP entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityIn == entityPlayer) {
            this.bipedHead.rotateAngleX = (prevRenderPitchHead + (renderPitchHead - prevRenderPitchHead) * 1) / (180.0F / (float) Math.PI);
        }
    }

}
