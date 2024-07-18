package org.cef.callback;

class CefSchemeRegistrar_N extends CefNativeAdapter implements CefSchemeRegistrar {
    private final native boolean N_AddCustomScheme(String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7);

    CefSchemeRegistrar_N() {
    }

    @Override
    public boolean addCustomScheme(String schemeName, boolean isStandard, boolean isLocal, boolean isDisplayIsolated, boolean isSecure, boolean isCorsEnabled, boolean isCspBypassing, boolean isFetchEnabled) {
        try {
            return N_AddCustomScheme(schemeName, isStandard, isLocal, isDisplayIsolated, isSecure, isCorsEnabled, isCspBypassing, isFetchEnabled);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
            return false;
        }
    }
}
