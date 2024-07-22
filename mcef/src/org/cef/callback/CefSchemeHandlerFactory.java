package org.cef.callback;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

public interface CefSchemeHandlerFactory {
    CefResourceHandler create(CefBrowser cefBrowser, CefFrame cefFrame, String str, CefRequest cefRequest);
}
