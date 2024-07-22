package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;

public interface CefContextMenuHandler {
    void onBeforeContextMenu(CefBrowser cefBrowser, CefFrame cefFrame, CefContextMenuParams cefContextMenuParams, CefMenuModel cefMenuModel);

    boolean onContextMenuCommand(CefBrowser cefBrowser, CefFrame cefFrame, CefContextMenuParams cefContextMenuParams, int i, int i2);

    void onContextMenuDismissed(CefBrowser cefBrowser, CefFrame cefFrame);
}
