package org.cef.browser;

import org.cef.CefClient;

public class CefBrowserFactory {
    public static Class<? extends CefRenderer> Renderer;

    public static CefBrowser create(CefClient client, String url, boolean isOffscreenRendered, boolean isTransparent, CefRequestContext context) {
        return isOffscreenRendered ? new CefBrowserOsr(client, url, isTransparent, context) : null;
    }
}
