package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefJSDialogCallback;
import org.cef.misc.BoolRef;

public interface CefJSDialogHandler {

    public enum JSDialogType {
        JSDIALOGTYPE_ALERT,
        JSDIALOGTYPE_CONFIRM,
        JSDIALOGTYPE_PROMPT
    }

    boolean onJSDialog(CefBrowser cefBrowser, String str, JSDialogType jSDialogType, String str2, String str3, CefJSDialogCallback cefJSDialogCallback, BoolRef boolRef);

    boolean onBeforeUnloadDialog(CefBrowser cefBrowser, String str, boolean z, CefJSDialogCallback cefJSDialogCallback);

    void onResetDialogState(CefBrowser cefBrowser);

    void onDialogClosed(CefBrowser cefBrowser);
}
