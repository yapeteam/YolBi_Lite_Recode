package org.cef.callback;

class CefAuthCallback_N extends CefNativeAdapter implements CefAuthCallback {
    private final native void N_Continue(long j, String str, String str2);

    private final native void N_Cancel(long j);

    CefAuthCallback_N() {
    }

    protected void finalize() throws Throwable {
        cancel();
        super.finalize();
    }

    @Override
    public void Continue(String username, String password) {
        try {
            N_Continue(getNativeRef(null), username, password);
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
