package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefAuthCallback;
import org.cef.callback.CefRequestCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;

public interface CefRequestHandler {

    public enum TerminationStatus {
        TS_ABNORMAL_TERMINATION,
        TS_PROCESS_WAS_KILLED,
        TS_PROCESS_CRASHED,
        TS_PROCESS_OOM
    }

    boolean onBeforeBrowse(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, boolean z, boolean z2);

    CefResourceRequestHandler getResourceRequestHandler(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest cefRequest, boolean z, boolean z2, String str, BoolRef boolRef);

    boolean getAuthCredentials(CefBrowser cefBrowser, CefFrame cefFrame, boolean z, String str, int i, String str2, String str3, CefAuthCallback cefAuthCallback);

    boolean onQuotaRequest(CefBrowser cefBrowser, String str, long j, CefRequestCallback cefRequestCallback);

    boolean onCertificateError(CefBrowser cefBrowser, CefLoadHandler.ErrorCode errorCode, String str, CefRequestCallback cefRequestCallback);

    void onPluginCrashed(CefBrowser cefBrowser, String str);

    void onRenderProcessTerminated(CefBrowser cefBrowser, TerminationStatus terminationStatus);
}
