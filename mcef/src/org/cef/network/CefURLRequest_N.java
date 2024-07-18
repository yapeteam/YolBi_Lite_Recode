package org.cef.network;

import org.cef.callback.CefNative;
import org.cef.callback.CefURLRequestClient;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefURLRequest;

class CefURLRequest_N extends CefURLRequest implements CefNative {
    private long N_CefHandle = 0;
    private final CefRequest request_;
    private final CefURLRequestClient client_;

    private final native void N_Create(CefRequest cefRequest, CefURLRequestClient cefURLRequestClient);

    private final native void N_Dispose(long j);

    private final native CefURLRequest.Status N_GetRequestStatus(long j);

    private final native CefLoadHandler.ErrorCode N_GetRequestError(long j);

    private final native CefResponse N_GetResponse(long j);

    private final native void N_Cancel(long j);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefURLRequest_N(CefRequest request, CefURLRequestClient client) {
        this.request_ = request;
        this.client_ = client;
    }

    public static final CefURLRequest createNative(CefRequest request, CefURLRequestClient client) {
        CefURLRequest_N result = new CefURLRequest_N(request, client);
        try {
            result.N_Create(request, client);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
        if (result.N_CefHandle == 0) {
            return null;
        }
        return result;
    }

    @Override
    public void finalize() {
    }

    @Override
    public void dispose() {
        try {
            N_Dispose(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public CefRequest getRequest() {
        return this.request_;
    }

    @Override
    public CefURLRequestClient getClient() {
        return this.client_;
    }

    @Override
    public CefURLRequest.Status getRequestStatus() {
        try {
            return N_GetRequestStatus(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public CefLoadHandler.ErrorCode getRequestError() {
        try {
            return N_GetRequestError(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public CefResponse getResponse() {
        try {
            return N_GetResponse(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void cancel() {
        try {
            N_Cancel(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
