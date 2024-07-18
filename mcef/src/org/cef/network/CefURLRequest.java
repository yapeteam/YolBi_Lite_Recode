package org.cef.network;

import org.cef.callback.CefURLRequestClient;
import org.cef.handler.CefLoadHandler;

public abstract class CefURLRequest {

    public enum Status {
        UR_UNKNOWN,
        UR_SUCCESS,
        UR_IO_PENDING,
        UR_CANCELED,
        UR_FAILED
    }

    public abstract void finalize();

    public abstract void dispose();

    public abstract CefRequest getRequest();

    public abstract CefURLRequestClient getClient();

    public abstract Status getRequestStatus();

    public abstract CefLoadHandler.ErrorCode getRequestError();

    public abstract CefResponse getResponse();

    public abstract void cancel();

    public static final CefURLRequest create(CefRequest request, CefURLRequestClient client) {
        return CefURLRequest_N.createNative(request, client);
    }
}
