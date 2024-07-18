package org.cef.browser.mac;

import org.cef.browser.CefBrowserWindow;

import java.awt.*;

public class CefBrowserWindowMac implements CefBrowserWindow {
    @Override
    public long getWindowHandle(Component comp) {
        return 0L;
    }
}
