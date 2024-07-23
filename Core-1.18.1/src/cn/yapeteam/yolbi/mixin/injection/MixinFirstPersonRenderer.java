package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.*;
import cn.yapeteam.yolbi.utils.render.PoseUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

@Mixin(ItemInHandRenderer.class)
public class MixinFirstPersonRenderer {

    @Shadow
    public void renderItem(LivingEntity p_109323_, ItemStack p_109324_, ItemTransforms.TransformType p_109325_, boolean p_109326_, PoseStack p_109327_, MultiBufferSource p_109328_, int p_109329_) {
    }

    @Inject(
            method = "renderArmWithItem",
            desc = "(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            target = @Target(
                    value = "INVOKESTATIC",
                    target = "net/minecraft/util/Mth.sin(F)F",
                    ordinal = 6,
                    shift = Target.Shift.BEFORE
            )
    )
    private void onRender(
            @Local(source = "pPlayer", index = 1) AbstractClientPlayer pPlayer,
            @Local(source = "pPoseStack", index = 8) PoseStack pPoseStack,
            @Local(source = "pSwingProgress", index = 5) float pSwingProgress,
            @Local(source = "pStack", index = 6) ItemStack pStack,
            @Local(source = "flag3", index = 13) boolean flag3,
            @Local(source = "pBuffer", index = 9) MultiBufferSource pBuffer,
            @Local(source = "pCombinedLight", index = 10) int pCombinedLight
    ) {
        if (pPlayer.getOffhandItem().getItem() instanceof ShieldItem && Minecraft.getInstance().options.keyRight.isDown()) {
            int side = 1;
            pPoseStack.translate(side * 0.56, -0.52, -0.72);
            pPoseStack.translate(side * -0.1414214, 0.08, 0.1414214);
            PoseUtils.rotateDeg(pPoseStack, -102.25F, 1, 0, 0);
            PoseUtils.rotateDeg(pPoseStack, side * 13.365F, 0, 1, 0);
            PoseUtils.rotateDeg(pPoseStack, side * 78.050003F, 0, 0, 1);
            double f = Math.sin(pSwingProgress * pSwingProgress * Math.PI);
            double f1 = Math.sin(Math.sqrt(pSwingProgress) * Math.PI);
            PoseUtils.rotateDeg(pPoseStack, (float) (f * -20.0F), 0, 1, 0);
            PoseUtils.rotateDeg(pPoseStack, (float) (f1 * -20.0F), 0, 0, 1);
            PoseUtils.rotateDeg(pPoseStack, (float) (f1 * -80.0F), 1, 0, 0);
            this.renderItem(pPlayer, pStack, flag3 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, pPoseStack, pBuffer, pCombinedLight);
            pPoseStack.popPose();
            return;
        }
    }
}
