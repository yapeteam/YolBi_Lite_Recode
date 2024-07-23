package org.cef.browser;

import net.montoyo.mcef.utilities.Log;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class CefRenderer {
    protected static final ArrayList<Integer> GL_TEXTURES = new ArrayList<>();
    protected boolean transparent_;
    public int[] texture_id_ = new int[1];
    protected int view_width_ = 0;
    protected int view_height_ = 0;
    protected Rectangle popup_rect_ = new Rectangle(0, 0, 0, 0);
    protected Rectangle original_popup_rect_ = new Rectangle(0, 0, 0, 0);

    public static void dumpVRAMLeak() {
        Log.info(">>>>> MCEF: Beginning VRAM leak report", new Object[0]);
        GL_TEXTURES.forEach(tex -> {
            Log.warning(">>>>> MCEF: This texture has not been freed: " + tex, new Object[0]);
        });
        Log.info(">>>>> MCEF: End of VRAM leak report", new Object[0]);
    }

    public CefRenderer(boolean transparent) {
        this.transparent_ = transparent;
        initialize();
    }

    protected boolean isTransparent() {
        return this.transparent_;
    }

    protected abstract void initialize();

    public abstract void cleanup();

    public abstract void render(double x1, double y1, double x2, double y2);

    public void onPopupSize(Rectangle rect) {
        if (rect.width <= 0 || rect.height <= 0) {
            return;
        }
        this.original_popup_rect_ = rect;
        this.popup_rect_ = getPopupRectInWebView(this.original_popup_rect_);
    }

    protected Rectangle getPopupRectInWebView(Rectangle rc) {
        if (rc.x < 0) {
            rc.x = 0;
        }
        if (rc.y < 0) {
            rc.y = 0;
        }
        if (rc.x + rc.width > this.view_width_) {
            rc.x = this.view_width_ - rc.width;
        }
        if (rc.y + rc.height > this.view_height_) {
            rc.y = this.view_height_ - rc.height;
        }
        if (rc.x < 0) {
            rc.x = 0;
        }
        if (rc.y < 0) {
            rc.y = 0;
        }
        return rc;
    }

    public void clearPopupRects() {
        this.popup_rect_.setBounds(0, 0, 0, 0);
        this.original_popup_rect_.setBounds(0, 0, 0, 0);
    }

    public abstract void onPaint(boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height, boolean completeReRender);

    public int getViewWidth() {
        return this.view_width_;
    }

    public int getViewHeight() {
        return this.view_height_;
    }
}
