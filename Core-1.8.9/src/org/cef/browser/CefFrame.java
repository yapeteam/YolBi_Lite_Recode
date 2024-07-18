package org.cef.browser;

public interface CefFrame {
    void dispose();

    long getIdentifier();

    String getURL();

    String getName();

    boolean isMain();

    boolean isValid();

    boolean isFocused();

    CefFrame getParent();

    void executeJavaScript(String str, String str2, int i);
}
