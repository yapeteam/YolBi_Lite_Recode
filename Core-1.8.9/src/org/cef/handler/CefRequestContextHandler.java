package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.cef.network.CefWebPluginInfo;

public interface CefRequestContextHandler {
    boolean onBeforePluginLoad(String str, String str2, boolean z, String str3, CefWebPluginInfo cefWebPluginInfo);

    CefResourceRequestHandler getResourceRequestHandler(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, boolean z, boolean z2, String str, BoolRef boolRef);
}
