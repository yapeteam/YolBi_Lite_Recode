package org.cef.browser;

import java.awt.Component;
import java.awt.Point;
import java.util.Vector;
import org.cef.CefClient;
import org.cef.callback.CefPdfPrintCallback;
import org.cef.callback.CefRunFileDialogCallback;
import org.cef.callback.CefStringVisitor;
import org.cef.handler.CefDialogHandler;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefWindowHandler;
import org.cef.misc.CefPdfPrintSettings;
import org.cef.network.CefRequest;

public interface CefBrowser {
    void createImmediately();

    Component getUIComponent();

    CefClient getClient();

    CefRenderHandler getRenderHandler();

    CefWindowHandler getWindowHandler();

    boolean canGoBack();

    void goBack();

    boolean canGoForward();

    void goForward();

    boolean isLoading();

    void reload();

    void reloadIgnoreCache();

    void stopLoad();

    int getIdentifier();

    CefFrame getMainFrame();

    CefFrame getFocusedFrame();

    CefFrame getFrame(long j);

    CefFrame getFrame(String str);

    Vector<Long> getFrameIdentifiers();

    Vector<String> getFrameNames();

    int getFrameCount();

    boolean isPopup();

    boolean hasDocument();

    void viewSource();

    void getSource(CefStringVisitor cefStringVisitor);

    void getText(CefStringVisitor cefStringVisitor);

    void loadRequest(CefRequest cefRequest);

    void loadURL(String str);

    void loadString(String str, String str2);

    void executeJavaScript(String str, String str2, int i);

    String getURL();

    void close(boolean z);

    void setCloseAllowed();

    boolean doClose();

    void onBeforeClose();

    void setFocus(boolean z);

    void setWindowVisibility(boolean z);

    double getZoomLevel();

    void setZoomLevel(double d);

    void runFileDialog(CefDialogHandler.FileDialogMode fileDialogMode, String str, String str2, Vector<String> vector, int i, CefRunFileDialogCallback cefRunFileDialogCallback);

    void startDownload(String str);

    void print();

    void printToPDF(String str, CefPdfPrintSettings cefPdfPrintSettings, CefPdfPrintCallback cefPdfPrintCallback);

    void find(int i, String str, boolean z, boolean z2, boolean z3);

    void stopFinding(boolean z);

    CefBrowser getDevTools();

    CefBrowser getDevTools(Point point);

    void replaceMisspelling(String str);
}
