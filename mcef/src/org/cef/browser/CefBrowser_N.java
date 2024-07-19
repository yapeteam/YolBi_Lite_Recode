package org.cef.browser;

import org.cef.CefClient;
import org.cef.callback.*;
import org.cef.handler.CefClientHandler;
import org.cef.handler.CefDialogHandler;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefWindowHandler;
import org.cef.misc.CefPdfPrintSettings;
import org.cef.network.CefRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.util.Vector;

public abstract class CefBrowser_N extends CefNativeAdapter implements CefBrowser {
    private CefClient client_;
    private String url_;
    private CefRequestContext request_context_;
    private CefBrowser_N parent_;
    private Point inspectAt_;
    private boolean isPending_ = false;
    private CefBrowser_N devTools_ = null;
    private boolean closeAllowed_ = false;
    private boolean isClosed_ = false;

    protected abstract CefBrowser_N createDevToolsBrowser(CefClient cefClient, String str, CefRequestContext cefRequestContext, CefBrowser_N cefBrowser_N, Point point);

    private native boolean N_CreateBrowser(CefClientHandler cefClientHandler, long j, String str, boolean z, boolean z2, Component component, CefRequestContext cefRequestContext);

    private native boolean N_CreateDevTools(CefBrowser cefBrowser, CefClientHandler cefClientHandler, long j, boolean z, boolean z2, Component component, Point point);

    private native long N_GetWindowHandle(long j);

    private native boolean N_CanGoBack();

    private native void N_GoBack();

    private native boolean N_CanGoForward();

    private native void N_GoForward();

    private native boolean N_IsLoading();

    private native void N_Reload();

    private native void N_ReloadIgnoreCache();

    private native void N_StopLoad();

    private native int N_GetIdentifier();

    private native CefFrame N_GetMainFrame();

    private native CefFrame N_GetFocusedFrame();

    private native CefFrame N_GetFrame(long j);

    private native CefFrame N_GetFrame2(String str);

    private native Vector<Long> N_GetFrameIdentifiers();

    private native Vector<String> N_GetFrameNames();

    private native int N_GetFrameCount();

    private native boolean N_IsPopup();

    private native boolean N_HasDocument();

    private native void N_ViewSource();

    private native void N_GetSource(CefStringVisitor cefStringVisitor);

    private native void N_GetText(CefStringVisitor cefStringVisitor);

    private native void N_LoadRequest(CefRequest cefRequest);

    private native void N_LoadURL(String str);

    private native void N_ExecuteJavaScript(String str, String str2, int i);

    private native String N_GetURL();

    private native void N_Close(boolean z);

    private native void N_SetFocus(boolean z);

    private native void N_SetWindowVisibility(boolean z);

    private native double N_GetZoomLevel();

    private native void N_SetZoomLevel(double d);

    private native void N_RunFileDialog(CefDialogHandler.FileDialogMode fileDialogMode, String str, String str2, Vector<String> vector, int i, CefRunFileDialogCallback cefRunFileDialogCallback);

    private native void N_StartDownload(String str);

    private native void N_Print();

    private native void N_PrintToPDF(String str, CefPdfPrintSettings cefPdfPrintSettings, CefPdfPrintCallback cefPdfPrintCallback);

    private native void N_Find(int i, String str, boolean z, boolean z2, boolean z3);

    private native void N_StopFinding(boolean z);

    private native void N_CloseDevTools();

    private native void N_ReplaceMisspelling(String str);

    private native void N_WasResized(int i, int i2);

    private native void N_Invalidate();

    private native void N_SendKeyEvent(KeyEvent keyEvent);

    private native void N_SendMouseEvent(MouseEvent mouseEvent);

    private native void N_SendMouseWheelEvent(MouseWheelEvent mouseWheelEvent);

    private native void N_DragTargetDragEnter(CefDragData cefDragData, Point point, int i, int i2);

    private native void N_DragTargetDragOver(Point point, int i, int i2);

    private native void N_DragTargetDragLeave();

    private native void N_DragTargetDrop(Point point, int i);

    private native void N_DragSourceEndedAt(Point point, int i);

    private native void N_DragSourceSystemDragEnded();

    private native void N_UpdateUI(Rectangle rectangle, Rectangle rectangle2);

    private native void N_SetParent(long j, Component component);

    private native void N_NotifyMoveOrResizeStarted();

    public CefBrowser_N(CefClient client, String url, CefRequestContext context, CefBrowser_N parent, Point inspectAt) {
        this.client_ = client;
        this.url_ = url;
        this.request_context_ = context;
        this.parent_ = parent;
        this.inspectAt_ = inspectAt;
    }

    public String getUrl() {
        return this.url_;
    }

    public CefRequestContext getRequestContext() {
        return this.request_context_;
    }

    public CefBrowser_N getParentBrowser() {
        return this.parent_;
    }

    public Point getInspectAt() {
        return this.inspectAt_;
    }

    public boolean isClosed() {
        return this.isClosed_;
    }

    public CefClient getClient() {
        return this.client_;
    }

    public CefRenderHandler getRenderHandler() {
        return null;
    }

    public CefWindowHandler getWindowHandler() {
        return null;
    }

    public synchronized void setCloseAllowed() {
        this.closeAllowed_ = true;
    }

    public synchronized boolean doClose() {
        if (this.closeAllowed_) {
            return false;
        }
        SwingUtilities.invokeLater(() -> {
            Window root = (Window) SwingUtilities.getRoot(CefBrowser_N.this.getUIComponent());
            if (root != null) {
                root.dispatchEvent(new WindowEvent(root, CefMenuModel.MenuId.MENU_ID_SPELLCHECK_SUGGESTION_1));
            }
        });
        return true;
    }

    public synchronized void onBeforeClose() {
        this.isClosed_ = true;
        if (this.request_context_ != null) {
            this.request_context_.dispose();
        }
        if (this.parent_ != null) {
            this.parent_.closeDevTools();
            this.parent_.devTools_ = null;
            this.parent_ = null;
        }
    }

    public CefBrowser getDevTools() {
        return getDevTools(null);
    }

    public synchronized CefBrowser getDevTools(Point inspectAt) {
        if (this.devTools_ == null) {
            this.devTools_ = createDevToolsBrowser(this.client_, this.url_, this.request_context_, this, inspectAt);
        }
        return this.devTools_;
    }

    public void createBrowser(CefClientHandler clientHandler, long windowHandle, String url, boolean osr, boolean transparent, Component canvas, CefRequestContext context) {
        if (getNativeRef("CefBrowser") == 0 && !this.isPending_) {
            try {
                this.isPending_ = N_CreateBrowser(clientHandler, windowHandle, url, osr, transparent, canvas, context);
            } catch (UnsatisfiedLinkError err) {
                err.printStackTrace();
            }
        }
    }

    public final void createDevTools(CefBrowser_N parent, CefClientHandler clientHandler, long windowHandle, boolean osr, boolean transparent, Component canvas, Point inspectAt) {
        if (getNativeRef("CefBrowser") == 0 && !this.isPending_) {
            try {
                this.isPending_ = N_CreateDevTools(parent, clientHandler, windowHandle, osr, transparent, canvas, inspectAt);
            } catch (UnsatisfiedLinkError err) {
                err.printStackTrace();
            }
        }
    }

    protected final long getWindowHandle(long surfaceHandle) {
        try {
            return N_GetWindowHandle(surfaceHandle);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
            return 0L;
        }
    }

    protected void finalize() throws Throwable {
        close(true);
        super.finalize();
    }

    public boolean canGoBack() {
        try {
            return N_CanGoBack();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    public void goBack() {
        try {
            N_GoBack();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public boolean canGoForward() {
        try {
            return N_CanGoForward();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    public void goForward() {
        try {
            N_GoForward();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public boolean isLoading() {
        try {
            return N_IsLoading();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    public void reload() {
        try {
            N_Reload();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void reloadIgnoreCache() {
        try {
            N_ReloadIgnoreCache();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void stopLoad() {
        try {
            N_StopLoad();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public int getIdentifier() {
        try {
            return N_GetIdentifier();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return -1;
        }
    }

    public CefFrame getMainFrame() {
        try {
            return N_GetMainFrame();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    public CefFrame getFocusedFrame() {
        try {
            return N_GetFocusedFrame();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    public CefFrame getFrame(long identifier) {
        try {
            return N_GetFrame(identifier);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    public CefFrame getFrame(String name) {
        try {
            return N_GetFrame2(name);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    public Vector<Long> getFrameIdentifiers() {
        try {
            return N_GetFrameIdentifiers();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    public Vector<String> getFrameNames() {
        try {
            return N_GetFrameNames();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    public int getFrameCount() {
        try {
            return N_GetFrameCount();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return -1;
        }
    }

    public boolean isPopup() {
        try {
            return N_IsPopup();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    public boolean hasDocument() {
        try {
            return N_HasDocument();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return false;
        }
    }

    public void viewSource() {
        try {
            N_ViewSource();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void getSource(CefStringVisitor visitor) {
        try {
            N_GetSource(visitor);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void getText(CefStringVisitor visitor) {
        try {
            N_GetText(visitor);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void loadRequest(CefRequest request) {
        try {
            N_LoadRequest(request);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void loadURL(String url) {
        try {
            N_LoadURL(url);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void executeJavaScript(String code, String url, int line) {
        try {
            N_ExecuteJavaScript(code, url, line);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public String getURL() {
        try {
            return N_GetURL();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return "";
        }
    }

    public void close(boolean force) {
        try {
            N_Close(force);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void setFocus(boolean enable) {
        try {
            N_SetFocus(enable);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void setWindowVisibility(boolean visible) {
        try {
            N_SetWindowVisibility(visible);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public double getZoomLevel() {
        try {
            return N_GetZoomLevel();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return 0.0d;
        }
    }

    public void setZoomLevel(double zoomLevel) {
        try {
            N_SetZoomLevel(zoomLevel);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void runFileDialog(CefDialogHandler.FileDialogMode mode, String title, String defaultFilePath, Vector<String> acceptFilters, int selectedAcceptFilter, CefRunFileDialogCallback callback) {
        try {
            N_RunFileDialog(mode, title, defaultFilePath, acceptFilters, selectedAcceptFilter, callback);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void startDownload(String url) {
        try {
            N_StartDownload(url);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void print() {
        try {
            N_Print();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void printToPDF(String path, CefPdfPrintSettings settings, CefPdfPrintCallback callback) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path was null or empty");
        }
        try {
            N_PrintToPDF(path, settings, callback);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void find(int identifier, String searchText, boolean forward, boolean matchCase, boolean findNext) {
        try {
            N_Find(identifier, searchText, forward, matchCase, findNext);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void stopFinding(boolean clearSelection) {
        try {
            N_StopFinding(clearSelection);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    protected final void closeDevTools() {
        try {
            N_CloseDevTools();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public void replaceMisspelling(String word) {
        try {
            N_ReplaceMisspelling(word);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void wasResized(int width, int height) {
        try {
            N_WasResized(width, height);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void invalidate() {
        try {
            N_Invalidate();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void sendKeyEvent(KeyEvent e) {
        try {
            N_SendKeyEvent(e);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void sendMouseEvent(MouseEvent e) {
        try {
            N_SendMouseEvent(e);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void sendMouseWheelEvent(MouseWheelEvent e) {
        try {
            N_SendMouseWheelEvent(e);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void dragTargetDragEnter(CefDragData dragData, Point pos, int modifiers, int allowedOps) {
        try {
            N_DragTargetDragEnter(dragData, pos, modifiers, allowedOps);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void dragTargetDragOver(Point pos, int modifiers, int allowedOps) {
        try {
            N_DragTargetDragOver(pos, modifiers, allowedOps);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void dragTargetDragLeave() {
        try {
            N_DragTargetDragLeave();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void dragTargetDrop(Point pos, int modifiers) {
        try {
            N_DragTargetDrop(pos, modifiers);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    protected final void dragSourceEndedAt(Point pos, int operation) {
        try {
            N_DragSourceEndedAt(pos, operation);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    protected final void dragSourceSystemDragEnded() {
        try {
            N_DragSourceSystemDragEnded();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void updateUI(Rectangle contentRect, Rectangle browserRect) {
        try {
            N_UpdateUI(contentRect, browserRect);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void setParent(long windowHandle, Component canvas) {
        try {
            N_SetParent(windowHandle, canvas);
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }

    public final void notifyMoveOrResizeStarted() {
        try {
            N_NotifyMoveOrResizeStarted();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
        }
    }
}
