package cn.yapeteam.yolbi.utils.render;

import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
public class GradientBlur {
    @SuppressWarnings("InnerClassMayBeStatic")
    public class Timer {
        private long lastCheck = getSystemTime();

        public boolean hasReach(float mil) {
            return getTimePassed() >= (mil);
        }

        public void reset() {
            lastCheck = getSystemTime();
        }

        private long getTimePassed() {
            return getSystemTime() - lastCheck;
        }

        private long getSystemTime() {
            return System.nanoTime() / (long) (1E6);
        }
    }

    @Setter
    private float x, y;
    @Setter
    private int width, height, delay;
    private final Timer timer = new Timer();
    private int tRed, tGreen, tBlue;
    private int lasttRed, lasttGreen, lasttBlue;
    private int bRed, bGreen, bBlue;
    private int lastbRed, lastbGreen, lastbBlue;
    private int colorTop, colorTopRight, colorBottom, colorBottomRight;

    public void set(float x, float y, int width, int height, int delay) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        setDelay(delay);
    }

    public enum ColorMode {
        TOP,
        TOP_RIGHT,
        BOTTOM,
        BOTTOM_RIGHT,
        MIXED
    }

    public void update(ColorMode mode) {
        lasttRed = tRed;
        lasttGreen = tGreen;
        lasttBlue = tBlue;

        lastbRed = bRed;
        lastbGreen = bGreen;
        lastbBlue = bBlue;

        Color top, bottom;
        switch (mode) {
            default:
            case MIXED:
                top = ColorUtil.blend(ColorUtil.colorFromInt(colorTop), ColorUtil.colorFromInt(colorTopRight));
                bottom = ColorUtil.blend(ColorUtil.colorFromInt(colorBottom), ColorUtil.colorFromInt(colorBottomRight));
                break;
            case TOP:
                top = ColorUtil.colorFromInt(colorTop);
                bottom = ColorUtil.colorFromInt(colorBottom);
                break;
            case TOP_RIGHT:
                top = ColorUtil.colorFromInt(colorTopRight);
                bottom = ColorUtil.colorFromInt(colorBottomRight);
                break;
            case BOTTOM:
                top = ColorUtil.colorFromInt(colorBottom);
                bottom = ColorUtil.colorFromInt(colorTop);
                break;
            case BOTTOM_RIGHT:
                top = ColorUtil.colorFromInt(colorBottomRight);
                bottom = ColorUtil.colorFromInt(colorTopRight);
                break;
        }


        bRed += (int) (((bottom.getRed() - bRed) / (5)) + 0.1);
        bGreen += (int) (((bottom.getGreen() - bGreen) / (5)) + 0.1);
        bBlue += (int) (((bottom.getBlue() - bBlue) / (5)) + 0.1);

        tRed += (int) (((top.getRed() - tRed) / (5)) + 0.1);
        tGreen += (int) (((top.getGreen() - tGreen) / (5)) + 0.1);
        tBlue += (int) (((top.getBlue() - tBlue) / (5)) + 0.1);

        tRed = Math.min(tRed, 255);
        tGreen = Math.min(tGreen, 255);
        tBlue = Math.min(tBlue, 255);
        tRed = Math.max(tRed, 0);
        tGreen = Math.max(tGreen, 0);
        tBlue = Math.max(tBlue, 0);

        bRed = Math.min(bRed, 255);
        bGreen = Math.min(bGreen, 255);
        bBlue = Math.min(bBlue, 255);
        bRed = Math.max(bRed, 0);
        bGreen = Math.max(bGreen, 0);
        bBlue = Math.max(bBlue, 0);
    }

    public void getPixels() {
        if (timer.hasReach(delay)) {
            IntBuffer pixelBuffer;
            int[] pixelValues;
            int size = width * height;
            pixelBuffer = BufferUtils.createIntBuffer(size);
            pixelValues = new int[size];
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            pixelBuffer.clear();
            Minecraft mc = Minecraft.getMinecraft();
            int scaleFactor = 1;
            int k = mc.gameSettings.guiScale;
            if (k == 0) {
                k = 1000;
            }
            while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320
                    && mc.displayHeight / (scaleFactor + 1) >= 240) {
                ++scaleFactor;
            }
            GL11.glReadPixels((int) (x * scaleFactor), (int) ((mc.displayHeight - (y + 6) * scaleFactor)), width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            colorTop = pixelValues[0];
            colorTopRight = pixelValues[width - 1];
            colorBottom = pixelValues[(height - 1) * width - 1];
            colorBottomRight = pixelValues[height * width - 1];
            timer.reset();
        }
    }

    public int smoothAnimation(double current, double last) {
        float partialTicks = Objects.requireNonNull(ReflectUtil.Minecraft$getTimer(Minecraft.getMinecraft())).field_194148_c;
        return (int) (current * partialTicks + (last * (1.0f - partialTicks)));
    }

    public Color getTColor() {
        int tR = smoothAnimation(tRed, lasttRed);
        int tG = smoothAnimation(tGreen, lasttGreen);
        int tB = smoothAnimation(tBlue, lasttBlue);
        try {
            return new Color(tR, tG, tB);
        } catch (Exception e) {
            return new Color(0, 0, 0);
        }
    }

    public Color getBColor() {
        int bR = smoothAnimation(bRed, lastbRed);
        int bG = smoothAnimation(bGreen, lastbGreen);
        int bB = smoothAnimation(bBlue, lastbBlue);
        try {
            return new Color(bR, bG, bB);
        } catch (Exception e) {
            return new Color(0, 0, 0);
        }
    }
}
