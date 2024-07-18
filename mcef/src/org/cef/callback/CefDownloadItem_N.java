package org.cef.callback;

import java.util.Date;

class CefDownloadItem_N extends CefNativeAdapter implements CefDownloadItem {
    private final native boolean N_IsValid(long j);

    private final native boolean N_IsInProgress(long j);

    private final native boolean N_IsComplete(long j);

    private final native boolean N_IsCanceled(long j);

    private final native long N_GetCurrentSpeed(long j);

    private final native int N_GetPercentComplete(long j);

    private final native long N_GetTotalBytes(long j);

    private final native long N_GetReceivedBytes(long j);

    private final native Date N_GetStartTime(long j);

    private final native Date N_GetEndTime(long j);

    private final native String N_GetFullPath(long j);

    private final native int N_GetId(long j);

    private final native String N_GetURL(long j);

    private final native String N_GetSuggestedFileName(long j);

    private final native String N_GetContentDisposition(long j);

    private final native String N_GetMimeType(long j);

    CefDownloadItem_N() {
    }

    @Override
    public boolean isValid() {
        try {
            return N_IsValid(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isInProgress() {
        try {
            return N_IsInProgress(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isComplete() {
        try {
            return N_IsComplete(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isCanceled() {
        try {
            return N_IsCanceled(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    @Override
    public long getCurrentSpeed() {
        try {
            return N_GetCurrentSpeed(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0L;
        }
    }

    @Override
    public int getPercentComplete() {
        try {
            return N_GetPercentComplete(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getTotalBytes() {
        try {
            return N_GetTotalBytes(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0L;
        }
    }

    @Override
    public long getReceivedBytes() {
        try {
            return N_GetReceivedBytes(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0L;
        }
    }

    @Override
    public Date getStartTime() {
        try {
            return N_GetStartTime(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public Date getEndTime() {
        try {
            return N_GetEndTime(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFullPath() {
        try {
            return N_GetFullPath(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public int getId() {
        try {
            return N_GetId(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0;
        }
    }

    @Override
    public String getURL() {
        try {
            return N_GetURL(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getSuggestedFileName() {
        try {
            return N_GetSuggestedFileName(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getContentDisposition() {
        try {
            return N_GetContentDisposition(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getMimeType() {
        try {
            return N_GetMimeType(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }
}
