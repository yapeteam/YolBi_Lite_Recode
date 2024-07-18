package org.cef.handler;

import java.awt.Rectangle;
import org.cef.browser.CefBrowser;

public interface CefWindowHandler {
    Rectangle getRect(CefBrowser cefBrowser);

    void onMouseEvent(CefBrowser cefBrowser, int i, int i2, int i3, int i4, int i5);
}
