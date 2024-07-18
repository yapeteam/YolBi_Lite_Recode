package org.cef.callback;

public class CefNativeAdapter implements CefNative {
    private long N_CefHandle = 0;

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }
}
