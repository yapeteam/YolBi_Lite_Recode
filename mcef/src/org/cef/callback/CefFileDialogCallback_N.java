package org.cef.callback;

import java.util.Vector;

class CefFileDialogCallback_N extends CefNativeAdapter implements CefFileDialogCallback {
    private final native void N_Continue(long j, int i, Vector<String> vector);

    private final native void N_Cancel(long j);

    CefFileDialogCallback_N() {
    }

    protected void finalize() throws Throwable {
        Cancel();
        super.finalize();
    }

    @Override
    public void Continue(int selectedAcceptFilter, Vector<String> filePaths) {
        try {
            N_Continue(getNativeRef(null), selectedAcceptFilter, filePaths);
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
