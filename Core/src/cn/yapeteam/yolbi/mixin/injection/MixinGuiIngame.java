package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Local;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Target;
import cn.yapeteam.yolbi.YolBi;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(
            method = "renderGameOverlay",
            desc = "(F)V",
            target = @Target(
                    value = "INVOKEVIRTUAL",
                    target = "net/minecraft/client/gui/GuiIngame.renderHotbar(Lnet/minecraft/client/gui/ScaledResolution;F)V",
                    shift = Target.Shift.AFTER
            )
    )
    public void onRenderGameOverlay(@Local(source = "partialTicks", index = 1) float partialTicks, @Local(source = "scaledResolution", index = 2) ScaledResolution scaledResolution) {
        YolBi.instance.getFontManager().getPingFang12().drawString("YolBi-" + partialTicks, 10, 10, 0xFFFFFF);
    }
}
