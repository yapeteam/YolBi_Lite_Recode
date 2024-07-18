package org.cef.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.cef.handler.CefLoadHandler;

public abstract class CefResponse {
    public abstract void dispose();

    public abstract boolean isReadOnly();

    public abstract CefLoadHandler.ErrorCode getError();

    public abstract void setError(CefLoadHandler.ErrorCode errorCode);

    public abstract int getStatus();

    public abstract void setStatus(int i);

    public abstract String getStatusText();

    public abstract void setStatusText(String str);

    public abstract String getMimeType();

    public abstract void setMimeType(String str);

    public abstract String getHeaderByName(String str);

    public abstract void setHeaderByName(String str, String str2, boolean z);

    public abstract String getHeader(String str);

    public abstract void getHeaderMap(Map<String, String> map);

    public abstract void setHeaderMap(Map<String, String> map);

    public static final CefResponse create() {
        return CefResponse_N.createNative();
    }

    public String toString() {
        String returnValue = "\nHTTP-Response:\n  error: " + getError();
        String returnValue2 = ((returnValue + "\n  readOnly: " + isReadOnly()) + "\n    HTTP/1.1 " + getStatus() + " " + getStatusText()) + "\n    Content-Type: " + getMimeType();
        Map<String, String> headerMap = new HashMap<>();
        getHeaderMap(headerMap);
        Set<Map.Entry<String, String>> entrySet = headerMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            returnValue2 = returnValue2 + "    " + entry.getKey() + "=" + entry.getValue() + "\n";
        }
        return returnValue2;
    }
}
