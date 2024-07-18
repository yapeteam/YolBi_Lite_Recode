package org.cef.network;

import java.util.Vector;
import org.cef.callback.CefCompletionCallback;
import org.cef.callback.CefCookieVisitor;

public abstract class CefCookieManager {
    public abstract void dispose();

    public abstract void setSupportedSchemes(Vector<String> vector, boolean z);

    public abstract boolean visitAllCookies(CefCookieVisitor cefCookieVisitor);

    public abstract boolean visitUrlCookies(String str, boolean z, CefCookieVisitor cefCookieVisitor);

    public abstract boolean setCookie(String str, CefCookie cefCookie);

    public abstract boolean deleteCookies(String str, String str2);

    public abstract boolean flushStore(CefCompletionCallback cefCompletionCallback);

    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public static final CefCookieManager getGlobalManager() {
        return CefCookieManager_N.getGlobalManagerNative();
    }
}
