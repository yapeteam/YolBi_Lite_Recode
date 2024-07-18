package org.cef.callback;

import org.cef.misc.BoolRef;
import org.cef.network.CefCookie;

public interface CefCookieVisitor {
    boolean visit(CefCookie cefCookie, int i, int i2, BoolRef boolRef);
}
