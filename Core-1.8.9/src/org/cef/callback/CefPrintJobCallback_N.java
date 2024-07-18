package org.cef.callback;

class CefPrintJobCallback_N extends CefNativeAdapter implements CefPrintJobCallback {
    private final native void N_Continue(long j);

    CefPrintJobCallback_N() {
    }

    protected void finalize() throws Throwable {
        Continue();
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
}
