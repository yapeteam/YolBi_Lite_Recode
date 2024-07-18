package org.cef.handler;

import org.cef.browser.CefBrowser;

public interface CefFocusHandler {

    public enum FocusSource {
        FOCUS_SOURCE_NAVIGATION,
        FOCUS_SOURCE_SYSTEM
    }

    void onTakeFocus(CefBrowser cefBrowser, boolean z);

    boolean onSetFocus(CefBrowser cefBrowser, FocusSource focusSource);

    void onGotFocus(CefBrowser cefBrowser);
}
