package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;

public interface CefDownloadHandler {
    void onBeforeDownload(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, String str, CefBeforeDownloadCallback cefBeforeDownloadCallback);

    void onDownloadUpdated(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, CefDownloadItemCallback cefDownloadItemCallback);
}
