package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import static cn.yapeteam.yolbi.utils.player.RotationManager.*;

@Mixin(ModelBiped.class)
public class MixinModelBiped {
    @Shadow
    public ModelRenderer bipedHead;

    @Inject(
            method = "setRotationAngles", desc = "(FFFFFFLnet/minecraft/entity/Entity;)V",
            target = @Target(
                    value = "GETFIELD",
                    target = "net/minecraft/client/model/ModelBiped.bipedBody Lnet/minecraft/client/model/ModelRenderer;",
                    shift = Target.Shift.BEFORE
            )
    )
    public void setRotationAngles(@Local(source = "entityIn", index = 7) Entity entityIn) {
        if (entityIn == Minecraft.getMinecraft().player)
            bipedHead.rotateAngleX = (prevRenderPitchHead + (renderPitchHead - prevRenderPitchHead) * 1) / (180.0F / (float) Math.PI);
    }
}
