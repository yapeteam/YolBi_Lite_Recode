package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;

public interface CefLifeSpanHandler {
    boolean onBeforePopup(CefBrowser cefBrowser, CefFrame cefFrame, String str, String str2);

    void onAfterCreated(CefBrowser cefBrowser);

    void onAfterParentChanged(CefBrowser cefBrowser);

    boolean doClose(CefBrowser cefBrowser);

    void onBeforeClose(CefBrowser cefBrowser);
}
