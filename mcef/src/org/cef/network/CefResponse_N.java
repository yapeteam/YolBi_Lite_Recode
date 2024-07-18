package org.cef.network;

import java.util.Map;
import org.cef.callback.CefNative;
import org.cef.handler.CefLoadHandler;

class CefResponse_N extends CefResponse implements CefNative {
    private long N_CefHandle = 0;

    private static final native CefResponse_N N_Create();

    private final native void N_Dispose(long j);

    private final native boolean N_IsReadOnly(long j);

    private final native CefLoadHandler.ErrorCode N_GetError(long j);

    private final native void N_SetError(long j, CefLoadHandler.ErrorCode errorCode);

    private final native int N_GetStatus(long j);

    private final native void N_SetStatus(long j, int i);

    private final native String N_GetStatusText(long j);

    private final native void N_SetStatusText(long j, String str);

    private final native String N_GetMimeType(long j);

    private final native void N_SetMimeType(long j, String str);

    private final native String N_GetHeaderByName(long j, String str);

    private final native void N_SetHeaderByName(long j, String str, String str2, boolean z);

    private final native void N_GetHeaderMap(long j, Map<String, String> map);

    private final native void N_SetHeaderMap(long j, Map<String, String> map);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefResponse_N() {
    }

    public static CefResponse createNative() {
        try {
            return N_Create();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void dispose() {
        try {
            N_Dispose(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public boolean isReadOnly() {
        try {
            return N_IsReadOnly(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public CefLoadHandler.ErrorCode getError() {
        try {
            return N_GetError(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void setError(CefLoadHandler.ErrorCode errorCode) {
        try {
            N_SetError(this.N_CefHandle, errorCode);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public int getStatus() {
        try {
            return N_GetStatus(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0;
        }
    }

    @Override
    public void setStatus(int status) {
        try {
            N_SetStatus(this.N_CefHandle, status);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public String getStatusText() {
        try {
            return N_GetStatusText(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void setStatusText(String statusText) {
        try {
            N_SetStatusText(this.N_CefHandle, statusText);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public String getMimeType() {
        try {
            return N_GetMimeType(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void setMimeType(String mimeType) {
        try {
            N_SetMimeType(this.N_CefHandle, mimeType);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public String getHeaderByName(String name) {
        try {
            return N_GetHeaderByName(this.N_CefHandle, name);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void setHeaderByName(String name, String value, boolean overwrite) {
        try {
            N_SetHeaderByName(this.N_CefHandle, name, value, overwrite);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public void getHeaderMap(Map<String, String> headerMap) {
        try {
            N_GetHeaderMap(this.N_CefHandle, headerMap);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setHeaderMap(Map<String, String> headerMap) {
        try {
            N_SetHeaderMap(this.N_CefHandle, headerMap);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
