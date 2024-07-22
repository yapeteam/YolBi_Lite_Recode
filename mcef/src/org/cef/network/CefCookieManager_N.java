package org.cef.network;

import java.util.Vector;
import org.cef.callback.CefCompletionCallback;
import org.cef.callback.CefCookieVisitor;
import org.cef.callback.CefNative;

class CefCookieManager_N extends CefCookieManager implements CefNative {
    private long N_CefHandle = 0;
    private static CefCookieManager_N globalInstance = null;

    private static final native CefCookieManager_N N_GetGlobalManager();

    private final native void N_Dispose(long j);

    private final native void N_SetSupportedSchemes(long j, Vector<String> vector, boolean z);

    private final native boolean N_VisitAllCookies(long j, CefCookieVisitor cefCookieVisitor);

    private final native boolean N_VisitUrlCookies(long j, String str, boolean z, CefCookieVisitor cefCookieVisitor);

    private final native boolean N_SetCookie(long j, String str, CefCookie cefCookie);

    private final native boolean N_DeleteCookies(long j, String str, String str2);

    private final native boolean N_FlushStore(long j, CefCompletionCallback cefCompletionCallback);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefCookieManager_N() {
    }

    public static final synchronized CefCookieManager_N getGlobalManagerNative() {
        if (globalInstance != null && globalInstance.N_CefHandle != 0) {
            return globalInstance;
        }
        CefCookieManager_N result = null;
        try {
            result = N_GetGlobalManager();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
        globalInstance = result;
        return globalInstance;
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
    public void setSupportedSchemes(Vector<String> schemes, boolean includeDefaults) {
        try {
            N_SetSupportedSchemes(this.N_CefHandle, schemes, includeDefaults);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public boolean visitAllCookies(CefCookieVisitor visitor) {
        try {
            return N_VisitAllCookies(this.N_CefHandle, visitor);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean visitUrlCookies(String url, boolean includeHttpOnly, CefCookieVisitor visitor) {
        try {
            return N_VisitUrlCookies(this.N_CefHandle, url, includeHttpOnly, visitor);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setCookie(String url, CefCookie cookie) {
        try {
            return N_SetCookie(this.N_CefHandle, url, cookie);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCookies(String url, String cookieName) {
        try {
            return N_DeleteCookies(this.N_CefHandle, url, cookieName);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean flushStore(CefCompletionCallback handler) {
        try {
            return N_FlushStore(this.N_CefHandle, handler);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }
}
