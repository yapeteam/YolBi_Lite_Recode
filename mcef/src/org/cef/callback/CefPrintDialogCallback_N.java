package org.cef.callback;

import org.cef.misc.CefPrintSettings;

class CefPrintDialogCallback_N extends CefNativeAdapter implements CefPrintDialogCallback {
    private final native void N_Continue(long j, CefPrintSettings cefPrintSettings);

    private final native void N_Cancel(long j);

    CefPrintDialogCallback_N() {
    }

    protected void finalize() throws Throwable {
        cancel();
        super.finalize();
    }

    @Override
    public void Continue(CefPrintSettings settings) {
        try {
            N_Continue(getNativeRef(null), settings);
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
