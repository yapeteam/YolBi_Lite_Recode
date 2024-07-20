package org.cef.browser;

import org.cef.callback.CefNative;
import org.cef.handler.CefRequestContextHandler;

class CefRequestContext_N extends CefRequestContext implements CefNative {
    private static CefRequestContext_N globalInstance = null;
    private long N_CefHandle = 0;
    private CefRequestContextHandler handler = null;

    private static native CefRequestContext_N N_GetGlobalContext();

    private static native CefRequestContext_N N_CreateContext(CefRequestContextHandler cefRequestContextHandler);

    private native boolean N_IsGlobal();

    private native void N_CefRequestContext_DTOR();

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefRequestContext_N() {
    }

    public static CefRequestContext_N getGlobalContextNative() {
        CefRequestContext_N result = null;
        try {
            result = N_GetGlobalContext();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
        if (globalInstance == null) {
            globalInstance = result;
        } else if (globalInstance.N_CefHandle == result.N_CefHandle) {
            result.N_CefRequestContext_DTOR();
        }
        return globalInstance;
    }

    public static CefRequestContext_N createNative(CefRequestContextHandler handler) {
        CefRequestContext_N result = null;
        try {
            result = N_CreateContext(handler);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
        if (result != null) {
            result.handler = handler;
        }
        return result;
    }

    @Override
    public void dispose() {
        try {
            N_CefRequestContext_DTOR();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public boolean isGlobal() {
        try {
            return N_IsGlobal();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public CefRequestContextHandler getHandler() {
        return this.handler;
    }
}
