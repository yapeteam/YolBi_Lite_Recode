package org.cef.handler;

import java.util.Vector;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefFileDialogCallback;

public interface CefDialogHandler {

    public enum FileDialogMode {
        FILE_DIALOG_OPEN,
        FILE_DIALOG_OPEN_MULTIPLE,
        FILE_DIALOG_SAVE
    }

    boolean onFileDialog(CefBrowser cefBrowser, FileDialogMode fileDialogMode, String str, String str2, Vector<String> vector, int i, CefFileDialogCallback cefFileDialogCallback);
}
