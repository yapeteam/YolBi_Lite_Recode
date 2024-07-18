package org.cef.browser;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;
import org.cef.callback.CefDragData;

class CefDropTargetListenerOsr implements DropTargetListener {
    private CefBrowserOsr browser_;
    private CefDragData dragData_ = null;
    private int dragOperations_ = 1;
    private int dragModifiers_ = 0;
    private int acceptOperations_ = 1;
    static final boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !CefDropTargetListenerOsr.class.desiredAssertionStatus();
    }

    CefDropTargetListenerOsr(CefBrowserOsr browser) {
        this.browser_ = browser;
    }

    public void dragEnter(DropTargetDragEvent event) {
        CreateDragData(event);
        this.browser_.dragTargetDragEnter(this.dragData_, event.getLocation(), this.dragModifiers_, this.dragOperations_);
    }

    public void dragExit(DropTargetEvent event) {
        AssertDragData();
        this.browser_.dragTargetDragLeave();
        ClearDragData();
    }

    public void dragOver(DropTargetDragEvent event) {
        AssertDragData();
        this.browser_.dragTargetDragOver(event.getLocation(), this.dragModifiers_, this.dragOperations_);
    }

    public void dropActionChanged(DropTargetDragEvent event) {
        AssertDragData();
        this.acceptOperations_ = event.getDropAction();
        switch (this.acceptOperations_) {
            case 0:
                this.dragOperations_ = 1;
                this.dragModifiers_ = 0;
                this.acceptOperations_ = 1;
                return;
            case 1:
                this.dragOperations_ = 1;
                this.dragModifiers_ = 4;
                return;
            case 2:
                this.dragOperations_ = 16;
                this.dragModifiers_ = 2;
                return;
            case 1073741824:
                this.dragOperations_ = 2;
                this.dragModifiers_ = 6;
                return;
            default:
                return;
        }
    }

    public void drop(DropTargetDropEvent event) {
        AssertDragData();
        this.browser_.dragTargetDrop(event.getLocation(), this.dragModifiers_);
        event.acceptDrop(this.acceptOperations_);
        event.dropComplete(true);
        ClearDragData();
    }

    private void CreateDragData(DropTargetDragEvent event) {
        if (!$assertionsDisabled && this.dragData_ != null) {
            throw new AssertionError();
        }
        this.dragData_ = createDragData(event);
        dropActionChanged(event);
    }

    private void AssertDragData() {
        if (!$assertionsDisabled && this.dragData_ == null) {
            throw new AssertionError();
        }
    }

    private void ClearDragData() {
        this.dragData_ = null;
    }

    private static CefDragData createDragData(DropTargetDragEvent event) {
        CefDragData dragData = CefDragData.create();
        Transferable transferable = event.getTransferable();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (DataFlavor flavor : flavors) {
            try {
                if (flavor.isFlavorJavaFileListType()) {
                    List<File> files = (List) transferable.getTransferData(flavor);
                    for (File file : files) {
                        dragData.addFile(file.getPath(), file.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dragData;
    }
}
