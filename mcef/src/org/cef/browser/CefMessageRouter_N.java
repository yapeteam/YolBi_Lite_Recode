package org.cef.browser;

import org.cef.callback.CefNative;
import org.cef.handler.CefMessageRouterHandler;

public class CefMessageRouter_N extends CefMessageRouter implements CefNative {
    private long N_CefHandle;

    private static native CefMessageRouter_N N_Create(CefMessageRouter.CefMessageRouterConfig cefMessageRouterConfig);

    private native void N_Dispose(long j);

    private native boolean N_AddHandler(long j, CefMessageRouterHandler cefMessageRouterHandler, boolean z);

    private native boolean N_RemoveHandler(long j, CefMessageRouterHandler cefMessageRouterHandler);

    private native void N_CancelPending(long j, CefBrowser cefBrowser, CefMessageRouterHandler cefMessageRouterHandler);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    private CefMessageRouter_N() {
        super(null);
        this.N_CefHandle = 0L;
    }

    public static CefMessageRouter createNative(CefMessageRouter.CefMessageRouterConfig config) {
        try {
            return N_Create(config);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
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
    public boolean addHandler(CefMessageRouterHandler handler, boolean first) {
        try {
            return N_AddHandler(this.N_CefHandle, handler, first);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeHandler(CefMessageRouterHandler handler) {
        try {
            return N_RemoveHandler(this.N_CefHandle, handler);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public void cancelPending(CefBrowser browser, CefMessageRouterHandler handler) {
        try {
            N_CancelPending(this.N_CefHandle, browser, handler);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public int getPendingCount(CefBrowser browser, CefMessageRouterHandler handler) {
        return 0;
    }
}
