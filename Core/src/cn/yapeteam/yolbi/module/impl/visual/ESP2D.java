package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.render.GraphicsUtils;
import cn.yapeteam.yolbi.utils.render.ESPUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
@ModuleInfo(name = "ESP2D", category = ModuleCategory.VISUAL)
public class ESP2D extends Module {
    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();

    @Listener
    private void onRender3D(EventRender3D e) {
        entityPosition.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList)
            if (ESPUtil.isInView(entity))
                entityPosition.put(entity, ESPUtil.getEntityPositionsOn2D(entity));
    }

    private final Color firstColor = Color.RED, secondColor = Color.BLUE, thirdColor = Color.BLACK, fourthColor = Color.WHITE;

    @Listener
    private void onRender2D(EventRender2D e) {
        if (mc.theWorld == null)
            return;
        for (Entity entity : entityPosition.keySet()) {
            YolBi.instance.getJFrameRenderer().getDrawables().add(g -> {
                if (entity instanceof EntityLivingBase) {
                    GraphicsUtils.setGraphicsContext(g);
                    Vector4f pos = entityPosition.get(entity);
                    if (pos == null) return;
                    float x = pos.getX(),
                            y = pos.getY(),
                            right = pos.getZ(),
                            bottom = pos.getW();
                    float outlineThickness = .5f;
                    //top
                    drawGradientLR(x, y, (right - x), 1, 1, firstColor, secondColor);
                    //left
                    drawGradientTB(x, y, 1, bottom - y, 1, firstColor, fourthColor);
                    //bottom
                    drawGradientLR(x, bottom, right - x, 1, 1, fourthColor, thirdColor);
                    //right
                    drawGradientTB(right, y, 1, (bottom - y) + 1, 1, secondColor, thirdColor);

                    //Outline

                    //top
                    drawRect(x - .5f, y - outlineThickness, (right - x) + 2, outlineThickness, Color.BLACK);
                    //Left
                    drawRect(x - outlineThickness, y, outlineThickness, (bottom - y) + 1, Color.BLACK);
                    //bottom
                    drawRect(x - .5f, (bottom + 1), (right - x) + 2, outlineThickness, Color.BLACK);
                    //Right
                    drawRect(right + 1, y, outlineThickness, (bottom - y) + 1, Color.BLACK);

                    //top
                    drawRect(x + 1, y + 1, (right - x) - 1, outlineThickness, Color.BLACK);
                    //Left
                    drawRect(x + 1, y + 1, outlineThickness, (bottom - y) - 1, Color.BLACK);
                    //bottom
                    drawRect(x + 1, (bottom - outlineThickness), (right - x) - 1, outlineThickness, Color.BLACK);
                    //Right
                    drawRect(right - outlineThickness, y + 1, outlineThickness, (bottom - y) - 1, Color.BLACK);
                }
            });
        }
    }

    private int process(float num) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return (int) num * scaledResolution.getScaleFactor();
    }

    private Color reAlpha(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255));
    }

    private void drawGradientLR(float x, float y, float width, float height, float alpha, Color color1, Color color2) {
        GraphicsUtils.horizontalGradientRect(process(x), process(y), process(width), process(height), reAlpha(color1, alpha), reAlpha(color2, alpha));
    }

    private void drawGradientTB(float x, float y, float width, float height, float alpha, Color color1, Color color2) {
        GraphicsUtils.verticalGradientRect(process(x), process(y), process(width), process(height), reAlpha(color1, alpha), reAlpha(color2, alpha));
    }

    private void drawRect(float x, float y, float width, float height, Color color) {
        GraphicsUtils.rect(process(x), process(y), process(width), process(height), color);
    }
}
