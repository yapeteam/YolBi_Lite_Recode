package org.cef.browser;

import org.cef.handler.CefRequestContextHandler;

public abstract class CefRequestContext {
    public abstract void dispose();

    public abstract boolean isGlobal();

    public abstract CefRequestContextHandler getHandler();

    public static CefRequestContext getGlobalContext() {
        return CefRequestContext_N.getGlobalContextNative();
    }

    public static CefRequestContext createContext(CefRequestContextHandler handler) {
        return CefRequestContext_N.createNative(handler);
    }
}
