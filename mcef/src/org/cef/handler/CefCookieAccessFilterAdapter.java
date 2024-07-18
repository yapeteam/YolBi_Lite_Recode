package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.network.CefCookie;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

public abstract class CefCookieAccessFilterAdapter implements CefCookieAccessFilter {
    @Override
    public boolean canSendCookie(CefBrowser browser, CefFrame frame, CefRequest request, CefCookie cookie) {
        return true;
    }

    @Override
    public boolean canSaveCookie(CefBrowser browser, CefFrame frame, CefRequest request, CefResponse response, CefCookie cookie) {
        return true;
    }
}
