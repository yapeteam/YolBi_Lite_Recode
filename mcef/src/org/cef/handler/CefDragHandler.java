package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefDragData;

public interface CefDragHandler {

    public static final class DragOperationMask {
        public static final int DRAG_OPERATION_NONE = 0;
        public static final int DRAG_OPERATION_COPY = 1;
        public static final int DRAG_OPERATION_LINK = 2;
        public static final int DRAG_OPERATION_GENERIC = 4;
        public static final int DRAG_OPERATION_PRIVATE = 8;
        public static final int DRAG_OPERATION_MOVE = 16;
        public static final int DRAG_OPERATION_DELETE = 32;
        public static final int DRAG_OPERATION_EVERY = Integer.MAX_VALUE;
    }

    boolean onDragEnter(CefBrowser cefBrowser, CefDragData cefDragData, int i);
}
