package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.handler.CefKeyboardHandler;
import org.cef.misc.BoolRef;

public abstract class CefKeyboardHandlerAdapter implements CefKeyboardHandler {
    @Override
    public boolean onPreKeyEvent(CefBrowser browser, CefKeyboardHandler.CefKeyEvent event, BoolRef is_keyboard_shortcut) {
        return false;
    }

    @Override
    public boolean onKeyEvent(CefBrowser browser, CefKeyboardHandler.CefKeyEvent event) {
        return false;
    }
}
