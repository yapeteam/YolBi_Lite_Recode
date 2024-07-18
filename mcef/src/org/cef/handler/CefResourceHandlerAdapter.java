package org.cef.handler;

import org.cef.callback.CefCallback;
import org.cef.callback.CefNativeAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

public abstract class CefResourceHandlerAdapter extends CefNativeAdapter implements CefResourceHandler {
    public boolean processRequest(CefRequest request, CefCallback callback) {
        return false;
    }

    public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
    }

    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        return false;
    }

    @Override
    public void cancel() {
    }
}
