package org.cef.callback;

public interface CefQueryCallback {
    void success(String str);

    void failure(int i, String str);
}
