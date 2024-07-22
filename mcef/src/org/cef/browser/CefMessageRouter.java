package org.cef.browser;

import org.cef.handler.CefMessageRouterHandler;

public abstract class CefMessageRouter {
    private CefMessageRouterConfig routerConfig_;

    public abstract void dispose();

    public abstract boolean addHandler(CefMessageRouterHandler cefMessageRouterHandler, boolean z);

    public abstract boolean removeHandler(CefMessageRouterHandler cefMessageRouterHandler);

    public abstract void cancelPending(CefBrowser cefBrowser, CefMessageRouterHandler cefMessageRouterHandler);

    public abstract int getPendingCount(CefBrowser cefBrowser, CefMessageRouterHandler cefMessageRouterHandler);

    public static class CefMessageRouterConfig {
        public String jsQueryFunction;
        public String jsCancelFunction;

        public CefMessageRouterConfig() {
            this("cefQuery", "cefQueryCancel");
        }

        public CefMessageRouterConfig(String queryFunction, String cancelFunction) {
            this.jsQueryFunction = queryFunction;
            this.jsCancelFunction = cancelFunction;
        }
    }

    public CefMessageRouter(CefMessageRouterConfig routerConfig) {
        this.routerConfig_ = routerConfig;
    }

    public static CefMessageRouter create() {
        return create(null, null);
    }

    public static CefMessageRouter create(CefMessageRouterConfig config) {
        return create(config, null);
    }

    public static CefMessageRouter create(CefMessageRouterHandler handler) {
        return create(null, handler);
    }

    public static CefMessageRouter create(CefMessageRouterConfig config, CefMessageRouterHandler handler) {
        CefMessageRouter router = CefMessageRouter_N.createNative(config);
        if (router != null && handler != null) {
            router.addHandler(handler, true);
        }
        return router;
    }

    public final CefMessageRouterConfig getMessageRouterConfig() {
        return this.routerConfig_;
    }

    public final void setMessageRouterConfig(CefMessageRouterConfig config) {
        this.routerConfig_ = config;
    }
}
