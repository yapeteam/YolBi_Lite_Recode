package cn.yapeteam.yolbi.mcef;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.utilities.Log;
import org.cef.browser.CefRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public class ImplCefRenderer extends CefRenderer {
    public ImplCefRenderer(boolean transparent) {
        super(transparent);
    }

    @Override
    protected void initialize() {
        GlStateManager.enableTexture2D();
        this.texture_id_[0] = GL11.glGenTextures();
        if (MCEF.CHECK_VRAM_LEAK)
            GL_TEXTURES.add(this.texture_id_[0]);
        GlStateManager.bindTexture(this.texture_id_[0]);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexEnvf(8960, 8704, 8448.0f);
        GlStateManager.bindTexture(0);
    }

    @Override
    public void cleanup() {
        if (this.texture_id_[0] != 0) {
            if (MCEF.CHECK_VRAM_LEAK) {
                GL_TEXTURES.remove(Integer.valueOf(this.texture_id_[0]));
            }
            GL11.glDeleteTextures(this.texture_id_[0]);
        }
    }

    @Override
    public void render(double x1, double y1, double x2, double y2) {
        if (this.view_width_ == 0 || this.view_height_ == 0)
            return;
        Tessellator t = Tessellator.getInstance();
        WorldRenderer vb = t.getWorldRenderer();
        GlStateManager.bindTexture(this.texture_id_[0]);
        vb.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        vb.pos(x1, y1, 0.0d).tex(0.0d, 1.0d).color(255, 255, 255, 255).endVertex();
        vb.pos(x2, y1, 0.0d).tex(1.0d, 1.0d).color(255, 255, 255, 255).endVertex();
        vb.pos(x2, y2, 0.0d).tex(1.0d, 0.0d).color(255, 255, 255, 255).endVertex();
        vb.pos(x1, y2, 0.0d).tex(0.0d, 0.0d).color(255, 255, 255, 255).endVertex();
        t.draw();
        GlStateManager.bindTexture(0);
    }

    @Override
    public void onPaint(boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height, boolean completeReRender) {
        if (this.transparent_)
            GlStateManager.enableBlend();
        int size = (width * height) << 2;
        if (size > buffer.limit()) {
            Log.warning("Bad data passed to CefRenderer.onPaint() triggered safe guards... (1)");
            return;
        }
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.texture_id_[0]);
        int oldAlignement = GL11.glGetInteger(3317);
        GL11.glPixelStorei(3317, 1);
        if (!popup) {
            if (completeReRender || width != this.view_width_ || height != this.view_height_) {
                this.view_width_ = width;
                this.view_height_ = height;
                GL11.glTexImage2D(3553, 0, 6408, this.view_width_, this.view_height_, 0, 32993, 5121, buffer);
            } else {
                GL11.glPixelStorei(3314, this.view_width_);
                for (Rectangle rect : dirtyRects) {
                    if (rect.x < 0 || rect.y < 0 || rect.x + rect.width > this.view_width_ || rect.y + rect.height > this.view_height_) {
                        Log.warning("Bad data passed to CefRenderer.onPaint() triggered safe guards... (2)");
                    } else {
                        GL11.glPixelStorei(3316, rect.x);
                        GL11.glPixelStorei(3315, rect.y);
                        GL11.glTexSubImage2D(3553, 0, rect.x, rect.y, rect.width, rect.height, 32993, 5121, buffer);
                    }
                }
                GL11.glPixelStorei(3316, 0);
                GL11.glPixelStorei(3315, 0);
                GL11.glPixelStorei(3314, 0);
            }
        } else if (this.popup_rect_.width > 0 && this.popup_rect_.height > 0) {
            int skip_pixels = 0;
            int x = this.popup_rect_.x;
            int skip_rows = 0;
            int y = this.popup_rect_.y;
            int w = width;
            int h = height;
            if (x < 0) {
                skip_pixels = -x;
                x = 0;
            }
            if (y < 0) {
                skip_rows = -y;
                y = 0;
            }
            if (x + w > this.view_width_) {
                w -= (x + w) - this.view_width_;
            }
            if (y + h > this.view_height_) {
                h -= (y + h) - this.view_height_;
            }
            GL11.glPixelStorei(3314, width);
            GL11.glPixelStorei(3316, skip_pixels);
            GL11.glPixelStorei(3315, skip_rows);
            GL11.glTexSubImage2D(3553, 0, x, y, w, h, 32993, 5121, buffer);
            GL11.glPixelStorei(3314, 0);
            GL11.glPixelStorei(3316, 0);
            GL11.glPixelStorei(3315, 0);
        }
        GL11.glPixelStorei(3317, oldAlignement);
        GlStateManager.bindTexture(0);
    }
}
