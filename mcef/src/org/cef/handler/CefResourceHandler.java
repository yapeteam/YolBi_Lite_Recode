package org.cef.handler;

import org.cef.callback.CefCallback;
import org.cef.callback.CefNative;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

public interface CefResourceHandler extends CefNative {
    boolean processRequest(CefRequest cefRequest, CefCallback cefCallback);

    void getResponseHeaders(CefResponse cefResponse, IntRef intRef, StringRef stringRef);

    boolean readResponse(byte[] bArr, int i, IntRef intRef, CefCallback cefCallback);

    void cancel();
}
