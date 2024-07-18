package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;

public abstract class CefLifeSpanHandlerAdapter implements CefLifeSpanHandler {
    @Override
    public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
        return false;
    }

    @Override
    public void onAfterCreated(CefBrowser browser) {
    }

    @Override
    public void onAfterParentChanged(CefBrowser browser) {
    }

    @Override
    public boolean doClose(CefBrowser browser) {
        return false;
    }

    @Override
    public void onBeforeClose(CefBrowser browser) {
    }
}
