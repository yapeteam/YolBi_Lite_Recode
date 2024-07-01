package cn.yapeteam.yolbi.render;

import java.awt.*;

public class GraphicsUtils {
    private static Graphics2D currentContext;

    public static void setGraphicsContext(Graphics context) {
        currentContext = (Graphics2D) context;
    }

    public static void setFont(Font font) {
        currentContext.setFont(font);
    }

    public static void rect(int x, int y, int width, int height, Color color) {
        currentContext.setColor(color);
        currentContext.fillRect(x, y, width, height);
    }

    public static void rect2(int left, int top, int right, int bottom, Color color) {
        rect(left, top, right - left, bottom - top, color);
    }

    public static void horizontalGradientRect(int x, int y, int width, int height, Color startColor, Color endColor) {
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {startColor, endColor};
        currentContext.setPaint(new LinearGradientPaint(x, y, x + width, y + .01f, fractions, colors));
        currentContext.fillRect(x, y, width, height);
    }

    public static void horizontalGradientRect2(int left, int top, int right, int bottom, Color startColor, Color endColor) {
        horizontalGradientRect(left, top, right - left, bottom - top, startColor, endColor);
    }

    public static void verticalGradientRect(int x, int y, int width, int height, Color startColor, Color endColor) {
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {startColor, endColor};
        currentContext.setPaint(new LinearGradientPaint(x, y, x + .01f, y + height, fractions, colors));
        currentContext.fillRect(x, y, width, height);
    }

    public static void verticalGradientRect2(int left, int top, int right, int bottom, Color startColor, Color endColor) {
        verticalGradientRect(left, top, right - left, bottom - top, startColor, endColor);
    }

    public static void drawString(String text, int x, int y, Color color) {
        currentContext.setColor(color);
        currentContext.drawString(text, x, y);
    }
}