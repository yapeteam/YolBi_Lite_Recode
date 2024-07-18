package org.cef.handler;

import java.util.HashMap;
import java.util.Map;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.network.CefRequest;

public interface CefLoadHandler {
    void onLoadingStateChange(CefBrowser cefBrowser, boolean z, boolean z2, boolean z3);

    void onLoadStart(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest.TransitionType transitionType);

    void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int i);

    void onLoadError(CefBrowser cefBrowser, CefFrame cefFrame, ErrorCode errorCode, String str, String str2);

    public enum ErrorCode {
        ERR_NONE(0),
        ERR_FAILED(-2),
        ERR_ABORTED(-3),
        ERR_INVALID_ARGUMENT(-4),
        ERR_INVALID_HANDLE(-5),
        ERR_FILE_NOT_FOUND(-6),
        ERR_TIMED_OUT(-7),
        ERR_FILE_TOO_BIG(-8),
        ERR_UNEXPECTED(-9),
        ERR_ACCESS_DENIED(-10),
        ERR_NOT_IMPLEMENTED(-11),
        ERR_CONNECTION_CLOSED(-100),
        ERR_CONNECTION_RESET(-101),
        ERR_CONNECTION_REFUSED(-102),
        ERR_CONNECTION_ABORTED(-103),
        ERR_CONNECTION_FAILED(-104),
        ERR_NAME_NOT_RESOLVED(-105),
        ERR_INTERNET_DISCONNECTED(-106),
        ERR_SSL_PROTOCOL_ERROR(-107),
        ERR_ADDRESS_INVALID(-108),
        ERR_ADDRESS_UNREACHABLE(-109),
        ERR_SSL_CLIENT_AUTH_CERT_NEEDED(-110),
        ERR_TUNNEL_CONNECTION_FAILED(-111),
        ERR_NO_SSL_VERSIONS_ENABLED(-112),
        ERR_SSL_VERSION_OR_CIPHER_MISMATCH(-113),
        ERR_SSL_RENEGOTIATION_REQUESTED(-114),
        ERR_CERT_COMMON_NAME_INVALID(-200),
        ERR_CERT_BEGIN(-200),
        ERR_CERT_DATE_INVALID(-201),
        ERR_CERT_AUTHORITY_INVALID(-202),
        ERR_CERT_CONTAINS_ERRORS(-203),
        ERR_CERT_NO_REVOCATION_MECHANISM(-204),
        ERR_CERT_UNABLE_TO_CHECK_REVOCATION(-205),
        ERR_CERT_REVOKED(-206),
        ERR_CERT_INVALID(-207),
        ERR_CERT_WEAK_SIGNATURE_ALGORITHM(-208),
        ERR_CERT_NON_UNIQUE_NAME(-210),
        ERR_CERT_WEAK_KEY(-211),
        ERR_CERT_NAME_CONSTRAINT_VIOLATION(-212),
        ERR_CERT_VALIDITY_TOO_LONG(-213),
        ERR_CERT_END(-213),
        ERR_INVALID_URL(-300),
        ERR_DISALLOWED_URL_SCHEME(-301),
        ERR_UNKNOWN_URL_SCHEME(-302),
        ERR_TOO_MANY_REDIRECTS(-310),
        ERR_UNSAFE_REDIRECT(-311),
        ERR_UNSAFE_PORT(-312),
        ERR_INVALID_RESPONSE(-320),
        ERR_INVALID_CHUNKED_ENCODING(-321),
        ERR_METHOD_NOT_SUPPORTED(-322),
        ERR_UNEXPECTED_PROXY_AUTH(-323),
        ERR_EMPTY_RESPONSE(-324),
        ERR_RESPONSE_HEADERS_TOO_BIG(-325),
        ERR_CACHE_MISS(-400),
        ERR_INSECURE_RESPONSE(-501);

        private static final Map<Integer, ErrorCode> CODES = new HashMap();
        private final int code;

        static {
            for (ErrorCode ec : values()) {
                if (!CODES.containsKey(Integer.valueOf(ec.code))) {
                    CODES.put(Integer.valueOf(ec.code), ec);
                }
            }
        }

        ErrorCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

        public static ErrorCode findByCode(int code) {
            return CODES.get(Integer.valueOf(code));
        }
    }
}
