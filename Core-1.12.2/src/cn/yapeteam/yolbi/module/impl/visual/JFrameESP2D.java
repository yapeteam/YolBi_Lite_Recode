package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventExternalRender;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.render.Drawable;
import cn.yapeteam.yolbi.render.GraphicsUtils;
import cn.yapeteam.yolbi.utils.render.ESPUtil;
import cn.yapeteam.yolbi.utils.vector.Vector4d;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("SameParameterValue")
public class JFrameESP2D extends Module implements Drawable {
    public JFrameESP2D() {
        super("JFrameESP2D", ModuleCategory.VISUAL);
    }

    private static final Color firstColor = Color.RED, secondColor = Color.BLUE, thirdColor = Color.PINK, fourthColor = Color.WHITE;

    @Override
    protected void onEnable() {
        YolBi.instance.getJFrameRenderer().getDrawables().add(this);
    }

    @Override
    protected void onDisable() {
        YolBi.instance.getJFrameRenderer().getDrawables().remove(this);
    }

    private final List<DrawableListener> cache = new CopyOnWriteArrayList<>();

    @Listener
    private void onExternal(EventExternalRender e) {
        cache.clear();
        try {
            if (mc.world != null) {
                for (int i = 0; i < mc.world.loadedEntityList.size(); i++) {
                    Entity entity = mc.world.loadedEntityList.get(i);
                    Vector4d pos = ESPUtil.get(entity);
                    if (pos == null) continue;
                    double left = pos.getX(),
                            top = pos.getY(),
                            right = pos.getZ(),
                            bottom = pos.getW();

                    float outlineThickness = .5f;

                    cache.add(g -> {
                        GraphicsUtils.setGraphicsContext(g);
                        //top
                        drawGradientLR(left, top, (right - left), 1, 1, firstColor, secondColor);
                        //left
                        drawGradientTB(left, top, 1, bottom - top, 1, firstColor, fourthColor);
                        //bottom
                        drawGradientLR(left, bottom, right - left, 1, 1, fourthColor, thirdColor);
                        //right
                        drawGradientTB(right, top, 1, (bottom - top) + 1, 1, secondColor, thirdColor);

                        //Outline

                        //top
                        drawRect(left - .5f, top - outlineThickness, (right - left) + 2, outlineThickness, Color.BLACK);
                        //Left
                        drawRect(left - outlineThickness, top, outlineThickness, (bottom - top) + 1, Color.BLACK);
                        //bottom
                        drawRect(left - .5f, (bottom + 1), (right - left) + 2, outlineThickness, Color.BLACK);
                        //Right
                        drawRect(right + 1, top, outlineThickness, (bottom - top) + 1, Color.BLACK);

                        //top
                        drawRect(left + 1, top + 1, (right - left) - 1, outlineThickness, Color.BLACK);
                        //Left
                        drawRect(left + 1, top + 1, outlineThickness, (bottom - top) - 1, Color.BLACK);
                        //bottom
                        drawRect(left + 1, (bottom - outlineThickness), (right - left) - 1, outlineThickness, Color.BLACK);
                        //Right
                        drawRect(right - outlineThickness, top + 1, outlineThickness, (bottom - top) - 1, Color.BLACK);

                        //Health Bar
                        EntityLivingBase livingBase = (EntityLivingBase) entity;
                        if (livingBase.getHealth() > 0) {
                            double healthHeight = (livingBase.getHealth() / livingBase.getMaxHealth()) * (bottom - top);
                            double x = right + 4, y = bottom - healthHeight;
                            //Outline
                            drawRect(x - outlineThickness, y - outlineThickness, 1 + 2 * outlineThickness, healthHeight + 2 * outlineThickness, Color.BLACK);
                            //Health
                            drawGradientTB(x, y, 1, healthHeight, 1, secondColor, thirdColor);
                        }
                    });
                }
            }
        } catch (Throwable ex) {
            Logger.exception(ex);
        }
    }

    @Override
    public List<DrawableListener> getDrawableListeners() {
        return cache;
    }

    private int process(double num) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return (int) (num * scaledResolution.getScaleFactor());
    }

    private Color reAlpha(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255));
    }

    private void drawGradientLR(double x, double y, double width, double height, float alpha, Color color1, Color color2) {
        GraphicsUtils.horizontalGradientRect(process(x), process(y), process(width), process(height), reAlpha(color1, alpha), reAlpha(color2, alpha));
    }

    private void drawGradientTB(double x, double y, double width, double height, float alpha, Color color1, Color color2) {
        GraphicsUtils.verticalGradientRect(process(x), process(y), process(width), process(height), reAlpha(color1, alpha), reAlpha(color2, alpha));
    }

    private void drawRect(double x, double y, double width, double height, Color color) {
        GraphicsUtils.rect(process(x), process(y), process(width), process(height), color);
    }
}