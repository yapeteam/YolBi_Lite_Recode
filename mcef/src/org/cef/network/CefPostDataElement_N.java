package org.cef.network;

import org.cef.callback.CefNative;
import org.cef.network.CefPostDataElement;

class CefPostDataElement_N extends CefPostDataElement implements CefNative {
    private long N_CefHandle = 0;

    private static final native CefPostDataElement_N N_Create();

    private final native void N_Dispose(long j);

    private final native boolean N_IsReadOnly(long j);

    private final native void N_SetToEmpty(long j);

    private final native void N_SetToFile(long j, String str);

    private final native void N_SetToBytes(long j, int i, byte[] bArr);

    private final native CefPostDataElement.Type N_GetType(long j);

    private final native String N_GetFile(long j);

    private final native int N_GetBytesCount(long j);

    private final native int N_GetBytes(long j, int i, byte[] bArr);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefPostDataElement_N() {
    }

    public static CefPostDataElement createNative() {
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
    public void setToEmpty() {
        try {
            N_SetToEmpty(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setToFile(String fileName) {
        try {
            N_SetToFile(this.N_CefHandle, fileName);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setToBytes(int size, byte[] bytes) {
        try {
            N_SetToBytes(this.N_CefHandle, size, bytes);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public CefPostDataElement.Type getType() {
        try {
            return N_GetType(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFile() {
        try {
            return N_GetFile(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public int getBytesCount() {
        try {
            return N_GetBytesCount(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getBytes(int size, byte[] bytes) {
        try {
            return N_GetBytes(this.N_CefHandle, size, bytes);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0;
        }
    }
}
