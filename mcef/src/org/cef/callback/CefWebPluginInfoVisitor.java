package org.cef.callback;

import org.cef.network.CefWebPluginInfo;

public interface CefWebPluginInfoVisitor {
    boolean visit(CefWebPluginInfo cefWebPluginInfo, int i, int i2);
}
