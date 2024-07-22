package org.cef.handler;

import java.awt.Dimension;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefNative;
import org.cef.callback.CefPrintDialogCallback;
import org.cef.callback.CefPrintJobCallback;
import org.cef.misc.CefPrintSettings;

public interface CefPrintHandler extends CefNative {
    void onPrintStart(CefBrowser cefBrowser);

    void onPrintSettings(CefPrintSettings cefPrintSettings, boolean z);

    boolean onPrintDialog(boolean z, CefPrintDialogCallback cefPrintDialogCallback);

    boolean onPrintJob(String str, String str2, CefPrintJobCallback cefPrintJobCallback);

    void onPrintReset();

    Dimension getPdfPaperSize(int i);
}
