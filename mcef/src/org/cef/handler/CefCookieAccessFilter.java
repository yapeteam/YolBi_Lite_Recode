package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.network.CefCookie;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

public interface CefCookieAccessFilter {
    boolean canSendCookie(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, CefCookie cefCookie);

    boolean canSaveCookie(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, CefResponse cefResponse, CefCookie cefCookie);
}
