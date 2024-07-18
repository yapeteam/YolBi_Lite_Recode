package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefJSDialogCallback;
import org.cef.handler.CefJSDialogHandler;
import org.cef.misc.BoolRef;

public abstract class CefJSDialogHandlerAdapter implements CefJSDialogHandler {
    @Override
    public boolean onJSDialog(CefBrowser browser, String origin_url, CefJSDialogHandler.JSDialogType dialog_type, String message_text, String default_prompt_text, CefJSDialogCallback callback, BoolRef suppress_message) {
        return false;
    }

    @Override
    public boolean onBeforeUnloadDialog(CefBrowser browser, String message_text, boolean is_reload, CefJSDialogCallback callback) {
        return false;
    }

    @Override
    public void onResetDialogState(CefBrowser browser) {
    }

    @Override
    public void onDialogClosed(CefBrowser browser) {
    }
}
