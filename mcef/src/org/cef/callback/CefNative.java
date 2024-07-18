package org.cef.callback;

public interface CefNative {
    void setNativeRef(String str, long j);

    long getNativeRef(String str);
}
