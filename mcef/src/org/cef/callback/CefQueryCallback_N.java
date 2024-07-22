package org.cef.callback;

class CefQueryCallback_N extends CefNativeAdapter implements CefQueryCallback {
    private final native void N_Success(long j, String str);

    private final native void N_Failure(long j, int i, String str);

    CefQueryCallback_N() {
    }

    protected void finalize() throws Throwable {
        failure(-1, "Unexpected call to CefQueryCallback_N::finalize()");
        super.finalize();
    }

    @Override
    public void success(String response) {
        try {
            N_Success(getNativeRef(null), response);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void failure(int error_code, String error_message) {
        try {
            N_Failure(getNativeRef(null), error_code, error_message);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
