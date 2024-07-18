package org.cef.network;

import org.cef.callback.CefWebPluginInfoVisitor;
import org.cef.callback.CefWebPluginUnstableCallback;

public abstract class CefWebPluginManager {
    public abstract void visitPlugins(CefWebPluginInfoVisitor cefWebPluginInfoVisitor);

    public abstract void refreshPlugins();

    public abstract void unregisterInternalPlugin(String str);

    public abstract void registerPluginCrash(String str);

    public abstract void isWebPluginUnstable(String str, CefWebPluginUnstableCallback cefWebPluginUnstableCallback);

    public static final CefWebPluginManager getGlobalManager() {
        return CefWebPluginManager_N.getInstance();
    }
}
