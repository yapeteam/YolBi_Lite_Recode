package org.cef.network;

import java.util.Map;
import org.cef.callback.CefNative;
import org.cef.network.CefRequest;

class CefRequest_N extends CefRequest implements CefNative {
    private long N_CefHandle = 0;

    private static final native CefRequest_N N_Create();

    private final native void N_Dispose(long j);

    private final native long N_GetIdentifier(long j);

    private final native boolean N_IsReadOnly(long j);

    private final native String N_GetURL(long j);

    private final native void N_SetURL(long j, String str);

    private final native String N_GetMethod(long j);

    private final native void N_SetMethod(long j, String str);

    private final native void N_SetReferrer(long j, String str, CefRequest.ReferrerPolicy referrerPolicy);

    private final native String N_GetReferrerURL(long j);

    private final native CefRequest.ReferrerPolicy N_GetReferrerPolicy(long j);

    private final native CefPostData N_GetPostData(long j);

    private final native void N_SetPostData(long j, CefPostData cefPostData);

    private final native String N_GetHeaderByName(long j, String str);

    private final native void N_SetHeaderByName(long j, String str, String str2, boolean z);

    private final native void N_GetHeaderMap(long j, Map<String, String> map);

    private final native void N_SetHeaderMap(long j, Map<String, String> map);

    private final native void N_Set(long j, String str, String str2, CefPostData cefPostData, Map<String, String> map);

    private final native int N_GetFlags(long j);

    private final native void N_SetFlags(long j, int i);

    private final native String N_GetFirstPartyForCookies(long j);

    private final native void N_SetFirstPartyForCookies(long j, String str);

    private final native CefRequest.ResourceType N_GetResourceType(long j);

    private final native CefRequest.TransitionType N_GetTransitionType(long j);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefRequest_N() {
    }

    public static CefRequest createNative() {
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
    public long getIdentifier() {
        try {
            return N_GetIdentifier(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0L;
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
    public String getURL() {
        try {
            return N_GetURL(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void setURL(String url) {
        try {
            N_SetURL(this.N_CefHandle, url);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public String getMethod() {
        try {
            return N_GetMethod(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void setMethod(String string) {
        try {
            N_SetMethod(this.N_CefHandle, string);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void setReferrer(String url, CefRequest.ReferrerPolicy policy) {
        try {
            N_SetReferrer(this.N_CefHandle, url, policy);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public String getReferrerURL() {
        try {
            return N_GetReferrerURL(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public CefRequest.ReferrerPolicy getReferrerPolicy() {
        try {
            return N_GetReferrerPolicy(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public CefPostData getPostData() {
        try {
            return N_GetPostData(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void setPostData(CefPostData postData) {
        try {
            N_SetPostData(this.N_CefHandle, postData);
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

    @Override
    public void set(String url, String method, CefPostData postData, Map<String, String> headerMap) {
        try {
            N_Set(this.N_CefHandle, url, method, postData, headerMap);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public int getFlags() {
        try {
            return N_GetFlags(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0;
        }
    }

    @Override
    public void setFlags(int flags) {
        try {
            N_SetFlags(this.N_CefHandle, flags);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public String getFirstPartyForCookies() {
        try {
            return N_GetFirstPartyForCookies(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public void setFirstPartyForCookies(String url) {
        try {
            N_SetFirstPartyForCookies(this.N_CefHandle, url);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public CefRequest.ResourceType getResourceType() {
        try {
            return N_GetResourceType(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return CefRequest.ResourceType.RT_MAIN_FRAME;
        }
    }

    @Override
    public CefRequest.TransitionType getTransitionType() {
        try {
            return N_GetTransitionType(this.N_CefHandle);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return CefRequest.TransitionType.TT_AUTO_SUBFRAME;
        }
    }
}
