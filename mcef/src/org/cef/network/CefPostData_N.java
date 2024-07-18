package org.cef.network;

import java.util.Vector;
import org.cef.callback.CefNative;

class CefPostData_N extends CefPostData implements CefNative {
    private long N_CefHandle = 0;

    private static final native CefPostData_N N_Create();

    private final native void N_Dispose(long j);

    private final native boolean N_IsReadOnly(long j);

    private final native int N_GetElementCount(long j);

    private final native void N_GetElements(long j, Vector<CefPostDataElement> vector);

    private final native boolean N_RemoveElement(long j, CefPostDataElement cefPostDataElement);

    private final native boolean N_AddElement(long j, CefPostDataElement cefPostDataElement);

    private final native void N_RemoveElements(long j);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefPostData_N() {
    }

    public static CefPostData createNative() {
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
    public int getElementCount() {
        try {
            return N_GetElementCount(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0;
        }
    }

    @Override
    public void getElements(Vector<CefPostDataElement> elements) {
        try {
            N_GetElements(this.N_CefHandle, elements);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public boolean removeElement(CefPostDataElement element) {
        try {
            return N_RemoveElement(this.N_CefHandle, element);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addElement(CefPostDataElement element) {
        try {
            return N_AddElement(this.N_CefHandle, element);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public void removeElements() {
        try {
            N_RemoveElements(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
