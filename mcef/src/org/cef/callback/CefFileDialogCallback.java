package org.cef.callback;

import java.util.Vector;

public interface CefFileDialogCallback {
    void Continue(int i, Vector<String> vector);

    void Cancel();
}
