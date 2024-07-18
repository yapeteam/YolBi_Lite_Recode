package org.cef;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserFactory;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefRequestContext;
import org.cef.callback.CefAuthCallback;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.callback.CefDragData;
import org.cef.callback.CefFileDialogCallback;
import org.cef.callback.CefJSDialogCallback;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefRequestCallback;
import org.cef.handler.CefClientHandler;
import org.cef.handler.CefContextMenuHandler;
import org.cef.handler.CefDialogHandler;
import org.cef.handler.CefDisplayHandler;
import org.cef.handler.CefDownloadHandler;
import org.cef.handler.CefDragHandler;
import org.cef.handler.CefFocusHandler;
import org.cef.handler.CefJSDialogHandler;
import org.cef.handler.CefKeyboardHandler;
import org.cef.handler.CefLifeSpanHandler;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefRequestHandler;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.handler.CefWindowHandler;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;

public class CefClient extends CefClientHandler implements CefContextMenuHandler, CefDialogHandler, CefDisplayHandler, CefDownloadHandler, CefDragHandler, CefFocusHandler, CefJSDialogHandler, CefKeyboardHandler, CefLifeSpanHandler, CefLoadHandler, CefRenderHandler, CefRequestHandler, CefWindowHandler {
    private HashMap<Integer, CefBrowser> browser_ = new HashMap<>();
    private CefContextMenuHandler contextMenuHandler_ = null;
    private CefDialogHandler dialogHandler_ = null;
    private CefDisplayHandler displayHandler_ = null;
    private CefDownloadHandler downloadHandler_ = null;
    private CefDragHandler dragHandler_ = null;
    private CefFocusHandler focusHandler_ = null;
    private CefJSDialogHandler jsDialogHandler_ = null;
    private CefKeyboardHandler keyboardHandler_ = null;
    private CefLifeSpanHandler lifeSpanHandler_ = null;
    private CefLoadHandler loadHandler_ = null;
    private CefRequestHandler requestHandler_ = null;
    private boolean isDisposed_ = false;
    private volatile CefBrowser focusedBrowser_ = null;
    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (CefClient.this.focusedBrowser_ != null) {
                Component browserUI = CefClient.this.focusedBrowser_.getUIComponent();
                Object oldUI = evt.getOldValue();
                if (CefClient.this.isPartOf(oldUI, browserUI)) {
                    CefClient.this.focusedBrowser_.setFocus(false);
                    CefClient.this.focusedBrowser_ = null;
                }
            }
        }
    };

    public CefClient() throws UnsatisfiedLinkError {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this.propertyChangeListener);
    }

    public boolean isPartOf(Object obj, Component browserUI) {
        if (obj == browserUI) {
            return true;
        }
        if (obj instanceof Container) {
            Component[] childs = ((Container) obj).getComponents();
            if (0 < childs.length) {
                Component child = childs[0];
                return isPartOf(child, browserUI);
            }
            return false;
        }
        return false;
    }

    @Override
    public void dispose() {
        this.isDisposed_ = true;
        cleanupBrowser(-1);
    }

    public CefBrowser createBrowser(String url, boolean isOffscreenRendered, boolean isTransparent) {
        return createBrowser(url, isOffscreenRendered, isTransparent, null);
    }

    public CefBrowser createBrowser(String url, boolean isOffscreenRendered, boolean isTransparent, CefRequestContext context) {
        if (this.isDisposed_) {
            throw new IllegalStateException("Can't create browser. CefClient is disposed");
        }
        return CefBrowserFactory.create(this, url, isOffscreenRendered, isTransparent, context);
    }

    @Override
    protected CefBrowser getBrowser(int identifier) {
        CefBrowser cefBrowser;
        synchronized (this.browser_) {
            cefBrowser = this.browser_.get(new Integer(identifier));
        }
        return cefBrowser;
    }

    @Override
    protected Object[] getAllBrowser() {
        Object[] array;
        synchronized (this.browser_) {
            array = this.browser_.values().toArray();
        }
        return array;
    }

    @Override
    protected CefContextMenuHandler getContextMenuHandler() {
        return this;
    }

    @Override
    protected CefDialogHandler getDialogHandler() {
        return this;
    }

    @Override
    protected CefDisplayHandler getDisplayHandler() {
        return this;
    }

    @Override
    protected CefDownloadHandler getDownloadHandler() {
        return this;
    }

    @Override
    protected CefDragHandler getDragHandler() {
        return this;
    }

    @Override
    protected CefFocusHandler getFocusHandler() {
        return this;
    }

    @Override
    protected CefJSDialogHandler getJSDialogHandler() {
        return this;
    }

    @Override
    protected CefKeyboardHandler getKeyboardHandler() {
        return this;
    }

    @Override
    protected CefLifeSpanHandler getLifeSpanHandler() {
        return this;
    }

    @Override
    protected CefLoadHandler getLoadHandler() {
        return this;
    }

    @Override
    protected CefRenderHandler getRenderHandler() {
        return this;
    }

    @Override
    protected CefRequestHandler getRequestHandler() {
        return this;
    }

    @Override
    protected CefWindowHandler getWindowHandler() {
        return this;
    }

    public CefClient addContextMenuHandler(CefContextMenuHandler handler) {
        if (this.contextMenuHandler_ == null) {
            this.contextMenuHandler_ = handler;
        }
        return this;
    }

    public void removeContextMenuHandler() {
        this.contextMenuHandler_ = null;
    }

    @Override
    public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
        if (this.contextMenuHandler_ != null && browser != null) {
            this.contextMenuHandler_.onBeforeContextMenu(browser, frame, params, model);
        }
    }

    @Override
    public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params, int commandId, int eventFlags) {
        if (this.contextMenuHandler_ != null && browser != null) {
            return this.contextMenuHandler_.onContextMenuCommand(browser, frame, params, commandId, eventFlags);
        }
        return false;
    }

    @Override
    public void onContextMenuDismissed(CefBrowser browser, CefFrame frame) {
        if (this.contextMenuHandler_ != null && browser != null) {
            this.contextMenuHandler_.onContextMenuDismissed(browser, frame);
        }
    }

    public CefClient addDialogHandler(CefDialogHandler handler) {
        if (this.dialogHandler_ == null) {
            this.dialogHandler_ = handler;
        }
        return this;
    }

    public void removeDialogHandler() {
        this.dialogHandler_ = null;
    }

    @Override
    public boolean onFileDialog(CefBrowser browser, CefDialogHandler.FileDialogMode mode, String title, String defaultFilePath, Vector<String> acceptFilters, int selectedAcceptFilter, CefFileDialogCallback callback) {
        if (this.dialogHandler_ != null && browser != null) {
            return this.dialogHandler_.onFileDialog(browser, mode, title, defaultFilePath, acceptFilters, selectedAcceptFilter, callback);
        }
        return false;
    }

    public CefClient addDisplayHandler(CefDisplayHandler handler) {
        if (this.displayHandler_ == null) {
            this.displayHandler_ = handler;
        }
        return this;
    }

    public void removeDisplayHandler() {
        this.displayHandler_ = null;
    }

    @Override
    public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
        if (this.displayHandler_ != null && browser != null) {
            this.displayHandler_.onAddressChange(browser, frame, url);
        }
    }

    @Override
    public void onTitleChange(CefBrowser browser, String title) {
        if (this.displayHandler_ != null && browser != null) {
            this.displayHandler_.onTitleChange(browser, title);
        }
    }

    @Override
    public boolean onTooltip(CefBrowser browser, String text) {
        if (this.displayHandler_ != null && browser != null) {
            return this.displayHandler_.onTooltip(browser, text);
        }
        return false;
    }

    @Override
    public void onStatusMessage(CefBrowser browser, String value) {
        if (this.displayHandler_ != null && browser != null) {
            this.displayHandler_.onStatusMessage(browser, value);
        }
    }

    @Override
    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
        if (this.displayHandler_ != null && browser != null) {
            return this.displayHandler_.onConsoleMessage(browser, level, message, source, line);
        }
        return false;
    }

    public CefClient addDownloadHandler(CefDownloadHandler handler) {
        if (this.downloadHandler_ == null) {
            this.downloadHandler_ = handler;
        }
        return this;
    }

    public void removeDownloadHandler() {
        this.downloadHandler_ = null;
    }

    @Override
    public void onBeforeDownload(CefBrowser browser, CefDownloadItem downloadItem, String suggestedName, CefBeforeDownloadCallback callback) {
        if (this.downloadHandler_ != null && browser != null) {
            this.downloadHandler_.onBeforeDownload(browser, downloadItem, suggestedName, callback);
        }
    }

    @Override
    public void onDownloadUpdated(CefBrowser browser, CefDownloadItem downloadItem, CefDownloadItemCallback callback) {
        if (this.downloadHandler_ != null && browser != null) {
            this.downloadHandler_.onDownloadUpdated(browser, downloadItem, callback);
        }
    }

    public CefClient addDragHandler(CefDragHandler handler) {
        if (this.dragHandler_ == null) {
            this.dragHandler_ = handler;
        }
        return this;
    }

    public void removeDragHandler() {
        this.dragHandler_ = null;
    }

    @Override
    public boolean onDragEnter(CefBrowser browser, CefDragData dragData, int mask) {
        if (this.dragHandler_ != null && browser != null) {
            return this.dragHandler_.onDragEnter(browser, dragData, mask);
        }
        return false;
    }

    public CefClient addFocusHandler(CefFocusHandler handler) {
        if (this.focusHandler_ == null) {
            this.focusHandler_ = handler;
        }
        return this;
    }

    public void removeFocusHandler() {
        this.focusHandler_ = null;
    }

    @Override
    public void onTakeFocus(CefBrowser browser, boolean next) {
        Component componentBefore;
        if (browser == null) {
            return;
        }
        browser.setFocus(false);
        Container parent = browser.getUIComponent().getParent();
        if (parent != null) {
            FocusTraversalPolicy policy = null;
            while (parent != null) {
                policy = parent.getFocusTraversalPolicy();
                if (policy != null) {
                    break;
                } else {
                    parent = parent.getParent();
                }
            }
            if (policy != null) {
                if (next) {
                    componentBefore = policy.getComponentAfter(parent, browser.getUIComponent());
                } else {
                    componentBefore = policy.getComponentBefore(parent, browser.getUIComponent());
                }
                Component nextComp = componentBefore;
                if (nextComp == null) {
                    policy.getDefaultComponent(parent).requestFocus();
                } else {
                    nextComp.requestFocus();
                }
            }
        }
        this.focusedBrowser_ = null;
        if (this.focusHandler_ != null) {
            this.focusHandler_.onTakeFocus(browser, next);
        }
    }

    @Override
    public boolean onSetFocus(CefBrowser browser, CefFocusHandler.FocusSource source) {
        if (browser == null) {
            return false;
        }
        boolean alreadyHandled = false;
        if (this.focusHandler_ != null) {
            alreadyHandled = this.focusHandler_.onSetFocus(browser, source);
        }
        return alreadyHandled;
    }

    @Override
    public void onGotFocus(CefBrowser browser) {
        if (browser == null) {
            return;
        }
        this.focusedBrowser_ = browser;
        browser.setFocus(true);
        if (this.focusHandler_ != null) {
            this.focusHandler_.onGotFocus(browser);
        }
    }

    public CefClient addJSDialogHandler(CefJSDialogHandler handler) {
        if (this.jsDialogHandler_ == null) {
            this.jsDialogHandler_ = handler;
        }
        return this;
    }

    public void removeJSDialogHandler() {
        this.jsDialogHandler_ = null;
    }

    @Override
    public boolean onJSDialog(CefBrowser browser, String origin_url, CefJSDialogHandler.JSDialogType dialog_type, String message_text, String default_prompt_text, CefJSDialogCallback callback, BoolRef suppress_message) {
        if (this.jsDialogHandler_ != null && browser != null) {
            return this.jsDialogHandler_.onJSDialog(browser, origin_url, dialog_type, message_text, default_prompt_text, callback, suppress_message);
        }
        return false;
    }

    @Override
    public boolean onBeforeUnloadDialog(CefBrowser browser, String message_text, boolean is_reload, CefJSDialogCallback callback) {
        if (this.jsDialogHandler_ != null && browser != null) {
            return this.jsDialogHandler_.onBeforeUnloadDialog(browser, message_text, is_reload, callback);
        }
        return false;
    }

    @Override
    public void onResetDialogState(CefBrowser browser) {
        if (this.jsDialogHandler_ != null && browser != null) {
            this.jsDialogHandler_.onResetDialogState(browser);
        }
    }

    @Override
    public void onDialogClosed(CefBrowser browser) {
        if (this.jsDialogHandler_ == null || browser == null) {
            return;
        }
        this.jsDialogHandler_.onDialogClosed(browser);
    }

    public CefClient addKeyboardHandler(CefKeyboardHandler handler) {
        if (this.keyboardHandler_ == null) {
            this.keyboardHandler_ = handler;
        }
        return this;
    }

    public void removeKeyboardHandler() {
        this.keyboardHandler_ = null;
    }

    @Override
    public boolean onPreKeyEvent(CefBrowser browser, CefKeyboardHandler.CefKeyEvent event, BoolRef is_keyboard_shortcut) {
        if (this.keyboardHandler_ != null && browser != null) {
            return this.keyboardHandler_.onPreKeyEvent(browser, event, is_keyboard_shortcut);
        }
        return false;
    }

    @Override
    public boolean onKeyEvent(CefBrowser browser, CefKeyboardHandler.CefKeyEvent event) {
        if (this.keyboardHandler_ != null && browser != null) {
            return this.keyboardHandler_.onKeyEvent(browser, event);
        }
        return false;
    }

    public CefClient addLifeSpanHandler(CefLifeSpanHandler handler) {
        if (this.lifeSpanHandler_ == null) {
            this.lifeSpanHandler_ = handler;
        }
        return this;
    }

    public void removeLifeSpanHandler() {
        this.lifeSpanHandler_ = null;
    }

    @Override
    public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
        if (this.isDisposed_) {
            return true;
        }
        if (this.lifeSpanHandler_ != null && browser != null) {
            return this.lifeSpanHandler_.onBeforePopup(browser, frame, target_url, target_frame_name);
        }
        return false;
    }

    @Override
    public void onAfterCreated(CefBrowser browser) {
        if (browser == null) {
            return;
        }
        Integer identifier = Integer.valueOf(browser.getIdentifier());
        synchronized (this.browser_) {
            this.browser_.put(identifier, browser);
        }
        if (this.lifeSpanHandler_ != null) {
            this.lifeSpanHandler_.onAfterCreated(browser);
        }
    }

    @Override
    public void onAfterParentChanged(CefBrowser browser) {
        if (browser != null && this.lifeSpanHandler_ != null) {
            this.lifeSpanHandler_.onAfterParentChanged(browser);
        }
    }

    @Override
    public boolean doClose(CefBrowser browser) {
        if (browser == null) {
            return false;
        }
        return this.lifeSpanHandler_ != null ? this.lifeSpanHandler_.doClose(browser) : browser.doClose();
    }

    @Override
    public void onBeforeClose(CefBrowser browser) {
        if (browser == null) {
            return;
        }
        if (this.lifeSpanHandler_ != null) {
            this.lifeSpanHandler_.onBeforeClose(browser);
        }
        browser.onBeforeClose();
        cleanupBrowser(browser.getIdentifier());
    }

    private void cleanupBrowser(int identifier) {
        synchronized (this.browser_) {
            if (identifier >= 0) {
                this.browser_.remove(Integer.valueOf(identifier));
            } else if (!this.browser_.isEmpty()) {
                Collection<CefBrowser> browserList = this.browser_.values();
                for (CefBrowser browser : browserList) {
                    browser.close(true);
                }
                return;
            }
            if (this.browser_.isEmpty() && this.isDisposed_) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(this.propertyChangeListener);
                removeContextMenuHandler(this);
                removeDialogHandler(this);
                removeDisplayHandler(this);
                removeDownloadHandler(this);
                removeDragHandler(this);
                removeFocusHandler(this);
                removeJSDialogHandler(this);
                removeKeyboardHandler(this);
                removeLifeSpanHandler(this);
                removeLoadHandler(this);
                removeRenderHandler(this);
                removeRequestHandler(this);
                removeWindowHandler(this);
                super.dispose();
                CefApp.getInstance().clientWasDisposed(this);
            }
        }
    }

    public CefClient addLoadHandler(CefLoadHandler handler) {
        if (this.loadHandler_ == null) {
            this.loadHandler_ = handler;
        }
        return this;
    }

    public void removeLoadHandler() {
        this.loadHandler_ = null;
    }

    @Override
    public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
        if (this.loadHandler_ != null && browser != null) {
            this.loadHandler_.onLoadingStateChange(browser, isLoading, canGoBack, canGoForward);
        }
    }

    @Override
    public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
        if (this.loadHandler_ != null && browser != null) {
            this.loadHandler_.onLoadStart(browser, frame, transitionType);
        }
    }

    @Override
    public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        if (this.loadHandler_ != null && browser != null) {
            this.loadHandler_.onLoadEnd(browser, frame, httpStatusCode);
        }
    }

    @Override
    public void onLoadError(CefBrowser browser, CefFrame frame, CefLoadHandler.ErrorCode errorCode, String errorText, String failedUrl) {
        if (this.loadHandler_ != null && browser != null) {
            this.loadHandler_.onLoadError(browser, frame, errorCode, errorText, failedUrl);
        }
    }

    @Override
    public synchronized void addMessageRouter(CefMessageRouter messageRouter) {
        super.addMessageRouter(messageRouter);
    }

    @Override
    public synchronized void removeMessageRouter(CefMessageRouter messageRouter) {
        super.removeMessageRouter(messageRouter);
    }

    @Override
    public Rectangle getViewRect(CefBrowser browser) {
        if (browser == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        CefRenderHandler realHandler = browser.getRenderHandler();
        return realHandler != null ? realHandler.getViewRect(browser) : new Rectangle(0, 0, 0, 0);
    }

    @Override
    public Point getScreenPoint(CefBrowser browser, Point viewPoint) {
        if (browser == null) {
            return new Point(0, 0);
        }
        CefRenderHandler realHandler = browser.getRenderHandler();
        return realHandler != null ? realHandler.getScreenPoint(browser, viewPoint) : new Point(0, 0);
    }

    @Override
    public void onPopupShow(CefBrowser browser, boolean show) {
        CefRenderHandler realHandler;
        if (browser != null && (realHandler = browser.getRenderHandler()) != null) {
            realHandler.onPopupShow(browser, show);
        }
    }

    @Override
    public void onPopupSize(CefBrowser browser, Rectangle size) {
        CefRenderHandler realHandler;
        if (browser != null && (realHandler = browser.getRenderHandler()) != null) {
            realHandler.onPopupSize(browser, size);
        }
    }

    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height) {
        CefRenderHandler realHandler;
        if (browser != null && (realHandler = browser.getRenderHandler()) != null) {
            realHandler.onPaint(browser, popup, dirtyRects, buffer, width, height);
        }
    }

    @Override
    public void onCursorChange(CefBrowser browser, int cursorType) {
        CefRenderHandler realHandler;
        if (browser != null && (realHandler = browser.getRenderHandler()) != null) {
            realHandler.onCursorChange(browser, cursorType);
        }
    }

    @Override
    public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
        CefRenderHandler realHandler;
        if (browser == null || (realHandler = browser.getRenderHandler()) == null) {
            return false;
        }
        return realHandler.startDragging(browser, dragData, mask, x, y);
    }

    @Override
    public void updateDragCursor(CefBrowser browser, int operation) {
        CefRenderHandler realHandler;
        if (browser != null && (realHandler = browser.getRenderHandler()) != null) {
            realHandler.updateDragCursor(browser, operation);
        }
    }

    public CefClient addRequestHandler(CefRequestHandler handler) {
        if (this.requestHandler_ == null) {
            this.requestHandler_ = handler;
        }
        return this;
    }

    public void removeRequestHandler() {
        this.requestHandler_ = null;
    }

    @Override
    public boolean onBeforeBrowse(CefBrowser browser, CefFrame frame, CefRequest request, boolean user_gesture, boolean is_redirect) {
        if (this.requestHandler_ != null && browser != null) {
            return this.requestHandler_.onBeforeBrowse(browser, frame, request, user_gesture, is_redirect);
        }
        return false;
    }

    @Override
    public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame, CefRequest request, boolean isNavigation, boolean isDownload, String requestInitiator, BoolRef disableDefaultHandling) {
        if (this.requestHandler_ != null && browser != null) {
            return this.requestHandler_.getResourceRequestHandler(browser, frame, request, isNavigation, isDownload, requestInitiator, disableDefaultHandling);
        }
        return null;
    }

    @Override
    public boolean getAuthCredentials(CefBrowser browser, CefFrame frame, boolean isProxy, String host, int port, String realm, String scheme, CefAuthCallback callback) {
        if (this.requestHandler_ != null && browser != null) {
            return this.requestHandler_.getAuthCredentials(browser, frame, isProxy, host, port, realm, scheme, callback);
        }
        return false;
    }

    @Override
    public boolean onQuotaRequest(CefBrowser browser, String origin_url, long new_size, CefRequestCallback callback) {
        if (this.requestHandler_ != null && browser != null) {
            return this.requestHandler_.onQuotaRequest(browser, origin_url, new_size, callback);
        }
        return false;
    }

    @Override
    public boolean onCertificateError(CefBrowser browser, CefLoadHandler.ErrorCode cert_error, String request_url, CefRequestCallback callback) {
        if (this.requestHandler_ != null) {
            return this.requestHandler_.onCertificateError(browser, cert_error, request_url, callback);
        }
        return false;
    }

    @Override
    public void onPluginCrashed(CefBrowser browser, String pluginPath) {
        if (this.requestHandler_ != null) {
            this.requestHandler_.onPluginCrashed(browser, pluginPath);
        }
    }

    @Override
    public void onRenderProcessTerminated(CefBrowser browser, CefRequestHandler.TerminationStatus status) {
        if (this.requestHandler_ != null) {
            this.requestHandler_.onRenderProcessTerminated(browser, status);
        }
    }

    @Override
    public Rectangle getRect(CefBrowser browser) {
        if (browser == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        CefWindowHandler realHandler = browser.getWindowHandler();
        return realHandler != null ? realHandler.getRect(browser) : new Rectangle(0, 0, 0, 0);
    }

    @Override
    public void onMouseEvent(CefBrowser browser, int event, int screenX, int screenY, int modifier, int button) {
        CefWindowHandler realHandler;
        if (browser != null && (realHandler = browser.getWindowHandler()) != null) {
            realHandler.onMouseEvent(browser, event, screenX, screenY, modifier, button);
        }
    }
}
