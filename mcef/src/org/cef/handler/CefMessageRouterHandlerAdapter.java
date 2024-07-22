package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefNativeAdapter;
import org.cef.callback.CefQueryCallback;

public abstract class CefMessageRouterHandlerAdapter extends CefNativeAdapter implements CefMessageRouterHandler {
    public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request, boolean persistent, CefQueryCallback callback) {
        return false;
    }

    public void onQueryCanceled(CefBrowser browser, CefFrame frame, long query_id) {
    }
}
