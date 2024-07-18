package org.cef.network;

import org.cef.callback.CefNative;
import org.cef.callback.CefWebPluginInfoVisitor;
import org.cef.callback.CefWebPluginUnstableCallback;

class CefWebPluginManager_N extends CefWebPluginManager implements CefNative {
    private long N_CefHandle = 0;
    private static CefWebPluginManager_N instance = null;

    private final native void N_VisitPlugins(CefWebPluginInfoVisitor cefWebPluginInfoVisitor);

    private final native void N_RefreshPlugins();

    private final native void N_UnregisterInternalPlugin(String str);

    private final native void N_RegisterPluginCrash(String str);

    private final native void N_IsWebPluginUnstable(String str, CefWebPluginUnstableCallback cefWebPluginUnstableCallback);

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        this.N_CefHandle = nativeRef;
    }

    @Override
    public long getNativeRef(String identifer) {
        return this.N_CefHandle;
    }

    CefWebPluginManager_N() {
    }

    public static synchronized CefWebPluginManager_N getInstance() {
        if (instance == null) {
            instance = new CefWebPluginManager_N();
        }
        return instance;
    }

    @Override
    public void visitPlugins(CefWebPluginInfoVisitor visitor) {
        try {
            N_VisitPlugins(visitor);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void refreshPlugins() {
        try {
            N_RefreshPlugins();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void unregisterInternalPlugin(String path) {
        try {
            N_UnregisterInternalPlugin(path);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void registerPluginCrash(String path) {
        try {
            N_RegisterPluginCrash(path);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    @Override
    public void isWebPluginUnstable(String path, CefWebPluginUnstableCallback callback) {
        try {
            N_IsWebPluginUnstable(path, callback);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
