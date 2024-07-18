package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.cef.network.CefWebPluginInfo;

public abstract class CefRequestContextHandlerAdapter implements CefRequestContextHandler {
    @Override
    public boolean onBeforePluginLoad(String mime_type, String plugin_url, boolean is_main_frame, String top_origin_url, CefWebPluginInfo plugin_info) {
        return false;
    }

    @Override
    public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame, CefRequest request, boolean isNavigation, boolean isDownload, String requestInitiator, BoolRef disableDefaultHandling) {
        return null;
    }
}
