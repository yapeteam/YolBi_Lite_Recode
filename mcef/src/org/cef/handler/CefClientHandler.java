package org.cef.handler;

import java.util.HashMap;
import java.util.Vector;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefNative;

public abstract class CefClientHandler implements CefNative {
    private HashMap<String, Long> N_CefHandle = new HashMap<>();
    private Vector<CefMessageRouter> msgRouters = new Vector<>();

    protected abstract CefBrowser getBrowser(int i);

    protected abstract Object[] getAllBrowser();

    protected abstract CefContextMenuHandler getContextMenuHandler();

    protected abstract CefDialogHandler getDialogHandler();

    protected abstract CefDisplayHandler getDisplayHandler();

    protected abstract CefDownloadHandler getDownloadHandler();

    protected abstract CefDragHandler getDragHandler();

    protected abstract CefFocusHandler getFocusHandler();

    protected abstract CefJSDialogHandler getJSDialogHandler();

    protected abstract CefKeyboardHandler getKeyboardHandler();

    protected abstract CefLifeSpanHandler getLifeSpanHandler();

    protected abstract CefLoadHandler getLoadHandler();

    protected abstract CefRenderHandler getRenderHandler();

    protected abstract CefRequestHandler getRequestHandler();

    protected abstract CefWindowHandler getWindowHandler();

    private final native void N_CefClientHandler_CTOR();

    private final native void N_addMessageRouter(CefMessageRouter cefMessageRouter);

    private final native void N_removeContextMenuHandler(CefContextMenuHandler cefContextMenuHandler);

    private final native void N_removeDialogHandler(CefDialogHandler cefDialogHandler);

    private final native void N_removeDisplayHandler(CefDisplayHandler cefDisplayHandler);

    private final native void N_removeDownloadHandler(CefDisplayHandler cefDisplayHandler);

    private final native void N_removeDragHandler(CefDragHandler cefDragHandler);

    private final native void N_removeFocusHandler(CefFocusHandler cefFocusHandler);

    private final native void N_removeJSDialogHandler(CefJSDialogHandler cefJSDialogHandler);

    private final native void N_removeKeyboardHandler(CefKeyboardHandler cefKeyboardHandler);

    private final native void N_removeLifeSpanHandler(CefLifeSpanHandler cefLifeSpanHandler);

    private final native void N_removeLoadHandler(CefLoadHandler cefLoadHandler);

    private final native void N_removeMessageRouter(CefMessageRouter cefMessageRouter);

    private final native void N_removeRenderHandler(CefRenderHandler cefRenderHandler);

    private final native void N_removeRequestHandler(CefRequestHandler cefRequestHandler);

    private final native void N_removeWindowHandler(CefWindowHandler cefWindowHandler);

    private final native void N_CefClientHandler_DTOR();

    @Override
    public void setNativeRef(String identifer, long nativeRef) {
        synchronized (this.N_CefHandle) {
            this.N_CefHandle.put(identifer, Long.valueOf(nativeRef));
        }
    }

    @Override
    public long getNativeRef(String identifer) {
        synchronized (this.N_CefHandle) {
            if (!this.N_CefHandle.containsKey(identifer)) {
                return 0L;
            }
            return this.N_CefHandle.get(identifer).longValue();
        }
    }

    public CefClientHandler() {
        try {
            N_CefClientHandler_CTOR();
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void dispose() {
        for (int i = 0; i < this.msgRouters.size(); i++) {
            try {
                this.msgRouters.get(i).dispose();
            } catch (UnsatisfiedLinkError err) {
                err.printStackTrace();
                return;
            }
        }
        this.msgRouters.clear();
        N_CefClientHandler_DTOR();
    }

    public synchronized void addMessageRouter(CefMessageRouter h) {
        try {
            this.msgRouters.add(h);
            N_addMessageRouter(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeContextMenuHandler(CefContextMenuHandler h) {
        try {
            N_removeContextMenuHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeDialogHandler(CefDialogHandler h) {
        try {
            N_removeDialogHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeDisplayHandler(CefDisplayHandler h) {
        try {
            N_removeDisplayHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeDownloadHandler(CefDisplayHandler h) {
        try {
            N_removeDownloadHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeDragHandler(CefDragHandler h) {
        try {
            N_removeDragHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeFocusHandler(CefFocusHandler h) {
        try {
            N_removeFocusHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeJSDialogHandler(CefJSDialogHandler h) {
        try {
            N_removeJSDialogHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeKeyboardHandler(CefKeyboardHandler h) {
        try {
            N_removeKeyboardHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeLifeSpanHandler(CefLifeSpanHandler h) {
        try {
            N_removeLifeSpanHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeLoadHandler(CefLoadHandler h) {
        try {
            N_removeLoadHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public synchronized void removeMessageRouter(CefMessageRouter h) {
        try {
            this.msgRouters.remove(h);
            N_removeMessageRouter(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeRenderHandler(CefRenderHandler h) {
        try {
            N_removeRenderHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeRequestHandler(CefRequestHandler h) {
        try {
            N_removeRequestHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }

    public void removeWindowHandler(CefWindowHandler h) {
        try {
            N_removeWindowHandler(h);
        } catch (UnsatisfiedLinkError err) {
            err.printStackTrace();
        }
    }
}
