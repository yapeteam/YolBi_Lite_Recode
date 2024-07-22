package org.cef.callback;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

public class CefDragData_N extends CefDragData implements CefNative {
    private long N_CefHandle = 0;

    private static final native CefDragData_N N_Create();

    private final native CefDragData_N N_Clone(long j);

    private final native void N_Dispose(long j);

    private final native boolean N_IsReadOnly(long j);

    private final native boolean N_IsLink(long j);

    private final native boolean N_IsFragment(long j);

    private final native boolean N_IsFile(long j);

    private final native String N_GetLinkURL(long j);

    private final native String N_GetLinkTitle(long j);

    private final native String N_GetLinkMetadata(long j);

    private final native String N_GetFragmentText(long j);

    private final native String N_GetFragmentHtml(long j);

    private final native String N_GetFragmentBaseURL(long j);

    private final native int N_GetFileContents(long j, OutputStream outputStream);

    private final native String N_GetFileName(long j);

    private final native boolean N_GetFileNames(long j, Vector<String> vector);

    private final native void N_SetLinkURL(long j, String str);

    private final native void N_SetLinkTitle(long j, String str);

    private final native void N_SetLinkMetadata(long j, String str);

    private final native void N_SetFragmentText(long j, String str);

    private final native void N_SetFragmentHtml(long j, String str);

    private final native void N_SetFragmentBaseURL(long j, String str);

    private final native void N_ResetFileContents(long j);

    private final native void N_AddFile(long j, String str, String str2);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefDragData_N() {
    }

    public static CefDragData createNative() {
        try {
            return N_Create();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public CefDragData mo24clone() {
        try {
            return N_Clone(this.N_CefHandle);
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
            return true;
        }
    }

    @Override
    public boolean isLink() {
        try {
            return N_IsLink(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isFragment() {
        try {
            return N_IsFragment(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isFile() {
        try {
            return N_IsFile(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public String getLinkURL() {
        try {
            return N_GetLinkURL(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLinkTitle() {
        try {
            return N_GetLinkTitle(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLinkMetadata() {
        try {
            return N_GetLinkMetadata(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFragmentText() {
        try {
            return N_GetFragmentText(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFragmentHtml() {
        try {
            return N_GetFragmentHtml(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFragmentBaseURL() {
        try {
            return N_GetFragmentBaseURL(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public int getFileContents(OutputStream writer) {
        try {
            return N_GetFileContents(this.N_CefHandle, writer);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0;
        }
    }

    @Override
    public String getFileName() {
        try {
            return N_GetFileName(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean getFileNames(Vector<String> names) {
        try {
            return N_GetFileNames(this.N_CefHandle, names);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public void setLinkURL(String url) {
        try {
            N_SetLinkURL(this.N_CefHandle, url);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setLinkTitle(String title) {
        try {
            N_SetLinkTitle(this.N_CefHandle, title);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setLinkMetadata(String data) {
        try {
            N_SetLinkMetadata(this.N_CefHandle, data);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setFragmentText(String text) {
        try {
            N_SetFragmentText(this.N_CefHandle, text);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setFragmentHtml(String html) {
        try {
            N_SetFragmentHtml(this.N_CefHandle, html);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setFragmentBaseURL(String baseUrl) {
        try {
            N_SetFragmentBaseURL(this.N_CefHandle, baseUrl);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void resetFileContents() {
        try {
            N_ResetFileContents(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void addFile(String path, String displayName) {
        try {
            N_AddFile(this.N_CefHandle, path, displayName);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public String toString() {
        Vector<String> names = new Vector<>();
        getFileNames(names);
        String fileNamesStr = "{";
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            String s = it.next();
            fileNamesStr = fileNamesStr + s + ",";
        }
        return "CefDragData_N [isLink()=" + isLink() + ", isFragment()=" + isFragment() + ", isFile()=" + isFile() + ", getLinkURL()=" + getLinkURL() + ", getLinkTitle()=" + getLinkTitle() + ", getLinkMetadata()=" + getLinkMetadata() + ", getFragmentText()=" + getFragmentText() + ", getFragmentHtml()=" + getFragmentHtml() + ", getFragmentBaseURL()=" + getFragmentBaseURL() + ", getFileName()=" + getFileName() + ", getFileNames(vector)=" + (fileNamesStr + "}") + "]";
    }
}
