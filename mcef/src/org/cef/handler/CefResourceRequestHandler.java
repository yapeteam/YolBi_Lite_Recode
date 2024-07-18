package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.misc.BoolRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.cef.network.CefURLRequest;

public interface CefResourceRequestHandler {
    CefCookieAccessFilter getCookieAccessFilter(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest);

    boolean onBeforeResourceLoad(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest);

    CefResourceHandler getResourceHandler(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest);

    void onResourceRedirect(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, CefResponse cefResponse, StringRef stringRef);

    boolean onResourceResponse(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, CefResponse cefResponse);

    void onResourceLoadComplete(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, CefResponse cefResponse, CefURLRequest.Status status, long j);

    void onProtocolExecution(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, BoolRef boolRef);
}
