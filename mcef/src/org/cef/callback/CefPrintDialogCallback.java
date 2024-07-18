package org.cef.callback;

import org.cef.misc.CefPrintSettings;

public interface CefPrintDialogCallback {
    void Continue(CefPrintSettings cefPrintSettings);

    void cancel();
}
