package cn.yapeteam.yolbi.font;

import com.mojang.blaze3d.vertex.PoseStack;

import java.awt.*;

public interface AbstractFontRenderer {
    double getStringWidth(String text);

    double getStringHeight(String text);

    void drawCenteredString(PoseStack stack, String text, double x, double y, int color);

    void drawCenteredString(PoseStack stack, String text, double x, double y, Color color);

    void drawString(PoseStack stack, String text, double x, double y, int color);

    void drawString(PoseStack stack, String text, double x, double y, Color color);

    double getHeight();

    Font getFont();
}
