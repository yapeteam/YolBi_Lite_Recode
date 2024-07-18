package org.cef.handler;

import java.awt.Point;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefDragData;

public abstract class CefRenderHandlerAdapter implements CefRenderHandler {
    @Override
    public void onCursorChange(CefBrowser browser, int cursorIdentifer) {
    }

    @Override
    public Rectangle getViewRect(CefBrowser browser) {
        return new Rectangle(0, 0, 0, 0);
    }

    @Override
    public Point getScreenPoint(CefBrowser browser, Point viewPoint) {
        return new Point(0, 0);
    }

    @Override
    public void onPopupShow(CefBrowser browser, boolean show) {
    }

    @Override
    public void onPopupSize(CefBrowser browser, Rectangle size) {
    }

    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height) {
    }

    @Override
    public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
        return false;
    }

    @Override
    public void updateDragCursor(CefBrowser browser, int operation) {
    }
}
