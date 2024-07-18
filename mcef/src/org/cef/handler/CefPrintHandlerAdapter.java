package org.cef.handler;

import java.awt.Dimension;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefNativeAdapter;
import org.cef.callback.CefPrintDialogCallback;
import org.cef.callback.CefPrintJobCallback;
import org.cef.misc.CefPrintSettings;

public abstract class CefPrintHandlerAdapter extends CefNativeAdapter implements CefPrintHandler {
    @Override
    public void onPrintStart(CefBrowser browser) {
    }

    @Override
    public void onPrintSettings(CefPrintSettings settings, boolean get_defaults) {
    }

    @Override
    public boolean onPrintDialog(boolean has_selection, CefPrintDialogCallback callback) {
        return false;
    }

    @Override
    public boolean onPrintJob(String document_name, String pdf_file_path, CefPrintJobCallback callback) {
        return false;
    }

    @Override
    public void onPrintReset() {
    }

    @Override
    public Dimension getPdfPaperSize(int deviceUnitsPerInch) {
        int adjustedWidth = (int) ((deviceUnitsPerInch / 300.0d) * 2480.0d);
        int adjustedHeight = (int) ((deviceUnitsPerInch / 300.0d) * 3508.0d);
        return new Dimension(adjustedWidth, adjustedHeight);
    }
}
