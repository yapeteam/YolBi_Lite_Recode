package org.cef.callback;

public interface CefDownloadItemCallback {
    void cancel();

    void pause();

    void resume();
}
