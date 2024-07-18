package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefNative;
import org.cef.callback.CefQueryCallback;

public interface CefMessageRouterHandler extends CefNative {
    boolean onQuery(CefBrowser cefBrowser, CefFrame cefFrame, long j, String str, boolean z, CefQueryCallback cefQueryCallback);

    void onQueryCanceled(CefBrowser cefBrowser, CefFrame cefFrame, long j);
}
