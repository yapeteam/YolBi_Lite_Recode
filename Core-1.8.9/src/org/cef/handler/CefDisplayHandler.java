package org.cef.handler;

import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;

public interface CefDisplayHandler {
    void onAddressChange(CefBrowser cefBrowser, CefFrame cefFrame, String str);

    void onTitleChange(CefBrowser cefBrowser, String str);

    boolean onTooltip(CefBrowser cefBrowser, String str);

    void onStatusMessage(CefBrowser cefBrowser, String str);

    boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String str, String str2, int i);
}
