package org.cef.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class CefRequest {

    public static final class CefUrlRequestFlags {
        public static final int UR_FLAG_NONE = 0;
        public static final int UR_FLAG_SKIP_CACHE = 1;
        public static final int UR_FLAG_ALLOW_CACHED_CREDENTIALS = 2;
        public static final int UR_FLAG_REPORT_UPLOAD_PROGRESS = 8;
        public static final int UR_FLAG_REPORT_RAW_HEADERS = 32;
        public static final int UR_FLAG_NO_DOWNLOAD_DATA = 64;
        public static final int UR_FLAG_NO_RETRY_ON_5XX = 128;
    }

    public enum ReferrerPolicy {
        REFERRER_POLICY_DEFAULT,
        REFERRER_POLICY_CLEAR_REFERRER_ON_TRANSITION_FROM_SECURE_TO_INSECURE,
        REFERRER_POLICY_REDUCE_REFERRER_GRANULARITY_ON_TRANSITION_CROSS_ORIGIN,
        REFERRER_POLICY_ORIGIN_ONLY_ON_TRANSITION_CROSS_ORIGIN,
        REFERRER_POLICY_NEVER_CLEAR_REFERRER,
        REFERRER_POLICY_ORIGIN,
        REFERRER_POLICY_CLEAR_REFERRER_ON_TRANSITION_CROSS_ORIGIN,
        REFERRER_POLICY_ORIGIN_CLEAR_ON_TRANSITION_FROM_SECURE_TO_INSECURE,
        REFERRER_POLICY_NO_REFERRER,
        REFERRER_POLICY_LAST_VALUE
    }

    public enum ResourceType {
        RT_MAIN_FRAME,
        RT_SUB_FRAME,
        RT_STYLESHEET,
        RT_SCRIPT,
        RT_IMAGE,
        RT_FONT_RESOURCE,
        RT_SUB_RESOURCE,
        RT_OBJECT,
        RT_MEDIA,
        RT_WORKER,
        RT_SHARED_WORKER,
        RT_PREFETCH,
        RT_FAVICON,
        RT_XHR,
        RT_PING,
        RT_SERVICE_WORKER
    }

    public abstract void dispose();

    public abstract long getIdentifier();

    public abstract boolean isReadOnly();

    public abstract String getURL();

    public abstract void setURL(String str);

    public abstract String getMethod();

    public abstract void setMethod(String str);

    public abstract void setReferrer(String str, ReferrerPolicy referrerPolicy);

    public abstract String getReferrerURL();

    public abstract ReferrerPolicy getReferrerPolicy();

    public abstract CefPostData getPostData();

    public abstract void setPostData(CefPostData cefPostData);

    public abstract String getHeaderByName(String str);

    public abstract void setHeaderByName(String str, String str2, boolean z);

    public abstract void getHeaderMap(Map<String, String> map);

    public abstract void setHeaderMap(Map<String, String> map);

    public abstract void set(String str, String str2, CefPostData cefPostData, Map<String, String> map);

    public abstract int getFlags();

    public abstract void setFlags(int i);

    public abstract String getFirstPartyForCookies();

    public abstract void setFirstPartyForCookies(String str);

    public abstract ResourceType getResourceType();

    public abstract TransitionType getTransitionType();

    public enum TransitionType {
        TT_LINK(0),
        TT_EXPLICIT(1),
        TT_AUTO_SUBFRAME(3),
        TT_MANUAL_SUBFRAME(4),
        TT_FORM_SUBMIT(7),
        TT_RELOAD(8);

        private int value;

        TransitionType(int source) {
            this.value = source;
        }

        public int getValue() {
            return this.value;
        }

        public int getSource() {
            return this.value & 255;
        }

        public void addQualifier(TransitionFlags flag) {
            this.value |= flag.getValue();
        }

        public void addQualifiers(int flags) {
            this.value |= flags & (-256);
        }

        public int getQualifiers() {
            return this.value & (-256);
        }

        public void removeQualifier(TransitionFlags flag) {
            this.value &= flag.getValue() ^ (-1);
        }

        public boolean isSet(TransitionFlags flag) {
            return (this.value & flag.getValue()) != 0;
        }

        public boolean isRedirect() {
            return (this.value & (-1073741824)) != 0;
        }
    }

    public enum TransitionFlags {
        TT_BLOCKED_FLAG(8388608),
        TT_FORWARD_BACK_FLAG(16777216),
        TT_CHAIN_START_FLAG(268435456),
        TT_CHAIN_END_FLAG(536870912),
        TT_CLIENT_REDIRECT_FLAG(1073741824),
        TT_SERVER_REDIRECT_FLAG(Integer.MIN_VALUE);

        private final int flag;

        TransitionFlags(int flag) {
            this.flag = flag;
        }

        public int getValue() {
            return this.flag;
        }
    }

    public static final CefRequest create() {
        return CefRequest_N.createNative();
    }

    public String toString() {
        String returnValue = "\nHTTP-Request\n  flags: " + getFlags();
        String returnValue2 = (((((returnValue + "\n  resourceType: " + getResourceType()) + "\n  transitionType: " + getTransitionType()) + "\n  firstPartyForCookies: " + getFirstPartyForCookies()) + "\n  referrerURL: " + getReferrerURL()) + "\n  referrerPolicy: " + getReferrerPolicy()) + "\n    " + getMethod() + " " + getURL() + " HTTP/1.1\n";
        Map<String, String> headerMap = new HashMap<>();
        getHeaderMap(headerMap);
        Set<Map.Entry<String, String>> entrySet = headerMap.entrySet();
        String mimeType = null;
        for (Map.Entry<String, String> entry : entrySet) {
            String key = entry.getKey();
            returnValue2 = returnValue2 + "    " + key + "=" + entry.getValue() + "\n";
            if (key.equals("Content-Type")) {
                mimeType = entry.getValue();
            }
        }
        CefPostData pd = getPostData();
        if (pd != null) {
            returnValue2 = returnValue2 + pd.toString(mimeType);
        }
        return returnValue2;
    }
}
