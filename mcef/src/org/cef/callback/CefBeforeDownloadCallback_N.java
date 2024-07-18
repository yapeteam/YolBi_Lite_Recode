package org.cef.callback;

class CefBeforeDownloadCallback_N extends CefNativeAdapter implements CefBeforeDownloadCallback {
    private final native void N_Continue(long j, String str, boolean z);

    CefBeforeDownloadCallback_N() {
    }

    @Override
    public void Continue(String downloadPath, boolean showDialog) {
        try {
            N_Continue(getNativeRef(null), downloadPath, showDialog);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
