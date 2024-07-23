package cn.yapeteam.yolbi.module.impl.visual.nametags;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.impl.visual.NameTags;
import cn.yapeteam.yolbi.utils.IMinecraft;
import cn.yapeteam.yolbi.utils.render.ColorUtil;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

public class DistanceShortNameTag implements IMinecraft {
    public static DecimalFormat format = new DecimalFormat("0.0");
    private static final ScaledResolution scaledResolution = new ScaledResolution(mc);

    public static void renderNameTag(EntityLivingBase entity, boolean invis, Map<EntityLivingBase, double[]> entityPositions, boolean armor, int alpha) {
        if (entity != mc.thePlayer && (invis || !entity.isInvisible())) {
            GlStateManager.pushMatrix();
            if (entity instanceof EntityPlayer) {
                double[] array = entityPositions.get(entity);
                if (array[3] < 0.0D || array[3] >= 1.0D) {
                    GlStateManager.popMatrix();
                    return;
                }
                AbstractFontRenderer wqy16 = YolBi.instance.getFontManager().getPingFang16();
                GlStateManager.translate(array[0] / scaledResolution.getScaleFactor(), array[1] / scaledResolution.getScaleFactor(), 0.0D);
                scale();
                GlStateManager.translate(0.0D, 5D, 0.0D);
                String string2 = NameTags.getTag(entity);
                float n = 60f;
                float n2 = (float) wqy16.getStringWidth(string2);
                float n3 = Math.max(n, n2);
                float n4 = n3 + 8.0f;
                RenderUtil.drawRect(-n4 / 2.0f, -25.0f, n4 / 2.0f, -8, new Color(20, 20, 20, alpha).getRGB());
                wqy16.drawStringWithShadow(string2, -n4 / 2.0f + 4.0f, -19.0f, -1);
                float n11 = (float) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount()) / (entity.getMaxHealth() + entity.getAbsorptionAmount());
                int color = NameTags.getColor(entity.getDisplayName().getFormattedText());
                RenderUtil.drawRect(-n4 / 2.0f, -9.5f, Math.min(n4, n4 / 2.0f - n4 / 2.0f * (1.0f - n11) * 2.0f), -8, ColorUtil.reAlpha(color, 0.8f));
                if (armor) NameTags.renderArmor(entity);
            }
            GlStateManager.popMatrix();
        }
    }

    private static void scale() {
        final float n = 1.0f;
        GlStateManager.scale(n, n, n);
    }

    public static int getColor(int p_clamp_int_0_, int p_clamp_int_0_2, int p_clamp_int_0_3, int p_clamp_int_0_4) {
        return MathHelper.clamp_int(p_clamp_int_0_4, 0, 255) << 24 | MathHelper.clamp_int(p_clamp_int_0_, 0, 255) << 16 | MathHelper.clamp_int(p_clamp_int_0_2, 0, 255) << 8 | MathHelper.clamp_int(p_clamp_int_0_3, 0, 255);
    }
}
