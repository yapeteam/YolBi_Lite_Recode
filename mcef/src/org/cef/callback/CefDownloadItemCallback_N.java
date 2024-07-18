package org.cef.callback;

class CefDownloadItemCallback_N extends CefNativeAdapter implements CefDownloadItemCallback {
    private final native void N_Dispose(long j);

    private final native void N_Cancel(long j);

    private final native void N_Pause(long j);

    private final native void N_Resume(long j);

    CefDownloadItemCallback_N() {
    }

    protected void finalize() throws Throwable {
        try {
            N_Dispose(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
        super.finalize();
    }

    @Override
    public void cancel() {
        try {
            N_Cancel(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            N_Pause(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void resume() {
        try {
            N_Resume(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
