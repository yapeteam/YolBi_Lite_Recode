package org.cef.network;

import java.util.Iterator;
import java.util.Vector;

public abstract class CefPostData {
    public abstract void dispose();

    public abstract boolean isReadOnly();

    public abstract int getElementCount();

    public abstract void getElements(Vector<CefPostDataElement> vector);

    public abstract boolean removeElement(CefPostDataElement cefPostDataElement);

    public abstract boolean addElement(CefPostDataElement cefPostDataElement);

    public abstract void removeElements();

    public static final CefPostData create() {
        return CefPostData_N.createNative();
    }

    public String toString() {
        return toString(null);
    }

    public String toString(String mimeType) {
        Vector<CefPostDataElement> elements = new Vector<>();
        getElements(elements);
        String returnValue = "";
        Iterator<CefPostDataElement> it = elements.iterator();
        while (it.hasNext()) {
            CefPostDataElement el = it.next();
            returnValue = returnValue + el.toString(mimeType) + "\n";
        }
        return returnValue;
    }
}
