package org.cef.callback;

import java.io.OutputStream;
import java.util.Vector;

public abstract class CefDragData {

    public static final class DragOperations {
        public static final int DRAG_OPERATION_NONE = 0;
        public static final int DRAG_OPERATION_COPY = 1;
        public static final int DRAG_OPERATION_LINK = 2;
        public static final int DRAG_OPERATION_GENERIC = 4;
        public static final int DRAG_OPERATION_PRIVATE = 8;
        public static final int DRAG_OPERATION_MOVE = 16;
        public static final int DRAG_OPERATION_DELETE = 32;
        public static final int DRAG_OPERATION_EVERY = Integer.MAX_VALUE;
    }

    public abstract CefDragData mo24clone();

    public abstract void dispose();

    public abstract boolean isReadOnly();

    public abstract boolean isLink();

    public abstract boolean isFragment();

    public abstract boolean isFile();

    public abstract String getLinkURL();

    public abstract String getLinkTitle();

    public abstract String getLinkMetadata();

    public abstract String getFragmentText();

    public abstract String getFragmentHtml();

    public abstract String getFragmentBaseURL();

    public abstract int getFileContents(OutputStream outputStream);

    public abstract String getFileName();

    public abstract boolean getFileNames(Vector<String> vector);

    public abstract void setLinkURL(String str);

    public abstract void setLinkTitle(String str);

    public abstract void setLinkMetadata(String str);

    public abstract void setFragmentText(String str);

    public abstract void setFragmentHtml(String str);

    public abstract void setFragmentBaseURL(String str);

    public abstract void resetFileContents();

    public abstract void addFile(String str, String str2);

    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public static CefDragData create() {
        return CefDragData_N.createNative();
    }

    public String toString() {
        return "CefDragData [isReadOnly()=" + isReadOnly() + ", isLink()=" + isLink() + ", isFragment()=" + isFragment() + ", isFile()=" + isFile() + ", getLinkURL()=" + getLinkURL() + ", getLinkTitle()=" + getLinkTitle() + ", getLinkMetadata()=" + getLinkMetadata() + ", getFragmentText()=" + getFragmentText() + ", getFragmentHtml()=" + getFragmentHtml() + ", getFragmentBaseURL()=" + getFragmentBaseURL() + ", getFileName()=" + getFileName() + "]";
    }
}
