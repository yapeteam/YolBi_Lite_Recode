package org.cef.callback;

import org.cef.network.CefURLRequest;

public interface CefURLRequestClient extends CefNative {
    void onRequestComplete(CefURLRequest cefURLRequest);

    void onUploadProgress(CefURLRequest cefURLRequest, int i, int i2);

    void onDownloadProgress(CefURLRequest cefURLRequest, int i, int i2);

    void onDownloadData(CefURLRequest cefURLRequest, byte[] bArr, int i);

    boolean getAuthCredentials(boolean z, String str, int i, String str2, String str3, CefAuthCallback cefAuthCallback);
}
