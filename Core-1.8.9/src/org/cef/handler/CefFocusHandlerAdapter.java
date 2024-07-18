package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.handler.CefFocusHandler;

public abstract class CefFocusHandlerAdapter implements CefFocusHandler {
    @Override
    public void onTakeFocus(CefBrowser browser, boolean next) {
    }

    @Override
    public boolean onSetFocus(CefBrowser browser, CefFocusHandler.FocusSource source) {
        return false;
    }

    @Override
    public void onGotFocus(CefBrowser browser) {
    }
}
