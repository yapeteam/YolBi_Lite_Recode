package org.cef.handler;

import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;

public abstract class CefDisplayHandlerAdapter implements CefDisplayHandler {
    @Override
    public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
    }

    @Override
    public void onTitleChange(CefBrowser browser, String title) {
    }

    @Override
    public boolean onTooltip(CefBrowser browser, String text) {
        return false;
    }

    @Override
    public void onStatusMessage(CefBrowser browser, String value) {
    }

    @Override
    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
        return false;
    }
}
