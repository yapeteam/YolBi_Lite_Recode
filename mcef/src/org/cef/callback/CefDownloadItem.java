package org.cef.callback;

import java.util.Date;

public interface CefDownloadItem {
    boolean isValid();

    boolean isInProgress();

    boolean isComplete();

    boolean isCanceled();

    long getCurrentSpeed();

    int getPercentComplete();

    long getTotalBytes();

    long getReceivedBytes();

    Date getStartTime();

    Date getEndTime();

    String getFullPath();

    int getId();

    String getURL();

    String getSuggestedFileName();

    String getContentDisposition();

    String getMimeType();
}
