package org.cef.handler;

import org.cef.CefApp;
import org.cef.callback.CefCommandLine;
import org.cef.callback.CefSchemeRegistrar;

public interface CefAppHandler {
    void onBeforeCommandLineProcessing(String str, CefCommandLine cefCommandLine);

    boolean onBeforeTerminate();

    void stateHasChanged(CefApp.CefAppState cefAppState);

    void onRegisterCustomSchemes(CefSchemeRegistrar cefSchemeRegistrar);

    void onContextInitialized();

    CefPrintHandler getPrintHandler();

    void onScheduleMessagePumpWork(long j);
}
