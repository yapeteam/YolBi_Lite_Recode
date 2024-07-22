package org.cef.callback;

class CefJSDialogCallback_N extends CefNativeAdapter implements CefJSDialogCallback {
    private final native void N_Continue(long j, boolean z, String str);

    CefJSDialogCallback_N() {
    }

    protected void finalize() throws Throwable {
        Continue(false, "");
        super.finalize();
    }

    @Override
    public void Continue(boolean success, String user_input) {
        try {
            N_Continue(getNativeRef(null), success, user_input);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
