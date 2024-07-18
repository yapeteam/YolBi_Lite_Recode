package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefAuthCallback;
import org.cef.callback.CefRequestCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefRequestHandler;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;

public abstract class CefRequestHandlerAdapter implements CefRequestHandler {
    @Override
    public boolean onBeforeBrowse(CefBrowser browser, CefFrame frame, CefRequest request, boolean user_gesture, boolean is_redirect) {
        return false;
    }

    @Override
    public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame, CefRequest request, boolean isNavigation, boolean isDownload, String requestInitiator, BoolRef disableDefaultHandling) {
        return null;
    }

    @Override
    public boolean getAuthCredentials(CefBrowser browser, CefFrame frame, boolean isProxy, String host, int port, String realm, String scheme, CefAuthCallback callback) {
        return false;
    }

    @Override
    public boolean onQuotaRequest(CefBrowser browser, String origin_url, long new_size, CefRequestCallback callback) {
        return false;
    }

    @Override
    public boolean onCertificateError(CefBrowser browser, CefLoadHandler.ErrorCode cert_error, String request_url, CefRequestCallback callback) {
        return false;
    }

    @Override
    public void onPluginCrashed(CefBrowser browser, String pluginPath) {
    }

    @Override
    public void onRenderProcessTerminated(CefBrowser browser, CefRequestHandler.TerminationStatus status) {
    }
}
