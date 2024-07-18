package org.cef.callback;

class CefRequestCallback_N extends CefNativeAdapter implements CefRequestCallback {
    private final native void N_Continue(long j, boolean z);

    private final native void N_Cancel(long j);

    CefRequestCallback_N() {
    }

    protected void finalize() throws Throwable {
        Cancel();
        super.finalize();
    }

    @Override
    public void Continue(boolean allow) {
        try {
            N_Continue(getNativeRef(null), allow);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void Cancel() {
        try {
            N_Cancel(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
