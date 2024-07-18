package org.cef.callback;

public interface CefAuthCallback {
    void Continue(String str, String str2);

    void cancel();
}
