package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Mixin;
import net.minecraft.client.renderer.ItemInHandRenderer;

@Mixin(ItemInHandRenderer.class)
public class MixinFirstPersonRenderer {
    @Inject(method = "renderArmWithItem", desc = "(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            target = );

}
