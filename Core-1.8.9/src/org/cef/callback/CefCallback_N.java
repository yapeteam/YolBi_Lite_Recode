package org.cef.callback;

class CefCallback_N extends CefNativeAdapter implements CefCallback {
    private final native void N_Continue(long j);

    private final native void N_Cancel(long j);

    CefCallback_N() {
    }

    protected void finalize() throws Throwable {
        cancel();
        super.finalize();
    }

    @Override
    public void Continue() {
        try {
            N_Continue(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        try {
            N_Cancel(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
