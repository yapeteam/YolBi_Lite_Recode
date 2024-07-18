package org.cef.network;

public abstract class CefPostDataElement {

    public enum Type {
        PDE_TYPE_EMPTY,
        PDE_TYPE_BYTES,
        PDE_TYPE_FILE
    }

    public abstract void dispose();

    public abstract boolean isReadOnly();

    public abstract void setToEmpty();

    public abstract void setToFile(String str);

    public abstract void setToBytes(int i, byte[] bArr);

    public abstract Type getType();

    public abstract String getFile();

    public abstract int getBytesCount();

    public abstract int getBytes(int i, byte[] bArr);

    public static final CefPostDataElement create() {
        return CefPostDataElement_N.createNative();
    }

    public String toString() {
        return toString(null);
    }

    public String toString(String mimeType) {
        int bytesCnt = getBytesCount();
        byte[] bytes = null;
        if (bytesCnt > 0) {
            bytes = new byte[bytesCnt];
        }
        boolean asText = false;
        if (mimeType != null) {
            if (mimeType.startsWith("text/")) {
                asText = true;
            } else if (mimeType.startsWith("application/xml")) {
                asText = true;
            } else if (mimeType.startsWith("application/xhtml")) {
                asText = true;
            } else if (mimeType.startsWith("application/x-www-form-urlencoded")) {
                asText = true;
            }
        }
        String returnValue = "";
        if (getType() == Type.PDE_TYPE_BYTES) {
            int setBytes = getBytes(bytes.length, bytes);
            String returnValue2 = returnValue + "    Content-Length: " + bytesCnt + "\n";
            if (asText) {
                returnValue2 = returnValue2 + "\n    " + new String(bytes);
            } else {
                for (int i = 0; i < setBytes; i++) {
                    if (i % 40 == 0) {
                        returnValue2 = returnValue2 + "\n    ";
                    }
                    returnValue2 = returnValue2 + String.format("%02X", Byte.valueOf(bytes[i])) + " ";
                }
            }
            returnValue = returnValue2 + "\n";
        } else if (getType() == Type.PDE_TYPE_FILE) {
            returnValue = returnValue + "\n    Bytes of file: " + getFile() + "\n";
        }
        return returnValue;
    }
}
