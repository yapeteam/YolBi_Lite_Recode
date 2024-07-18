package org.cef.network;

import org.cef.callback.CefNativeAdapter;

class CefWebPluginInfo_N extends CefNativeAdapter implements CefWebPluginInfo {
    private final native String N_GetName(long j);

    private final native String N_GetPath(long j);

    private final native String N_GetVersion(long j);

    private final native String N_GetDescription(long j);

    CefWebPluginInfo_N() {
    }

    @Override
    public String getName() {
        try {
            return N_GetName(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getPath() {
        try {
            return N_GetPath(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getVersion() {
        try {
            return N_GetVersion(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDescription() {
        try {
            return N_GetDescription(getNativeRef(null));
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }
}
