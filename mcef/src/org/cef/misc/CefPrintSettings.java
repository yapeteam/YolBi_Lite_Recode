package org.cef.misc;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;

public abstract class CefPrintSettings {

    public enum ColorModel {
        COLOR_MODEL_UNKNOWN,
        COLOR_MODEL_GRAY,
        COLOR_MODEL_COLOR,
        COLOR_MODEL_CMYK,
        COLOR_MODEL_CMY,
        COLOR_MODEL_KCMY,
        COLOR_MODEL_CMY_K,
        COLOR_MODEL_BLACK,
        COLOR_MODEL_GRAYSCALE,
        COLOR_MODEL_RGB,
        COLOR_MODEL_RGB16,
        COLOR_MODEL_RGBA,
        COLOR_MODEL_COLORMODE_COLOR,
        COLOR_MODEL_COLORMODE_MONOCHROME,
        COLOR_MODEL_HP_COLOR_COLOR,
        COLOR_MODEL_HP_COLOR_BLACK,
        COLOR_MODEL_PRINTOUTMODE_NORMAL,
        COLOR_MODEL_PRINTOUTMODE_NORMAL_GRAY,
        COLOR_MODEL_PROCESSCOLORMODEL_CMYK,
        COLOR_MODEL_PROCESSCOLORMODEL_GREYSCALE,
        COLOR_MODEL_PROCESSCOLORMODEL_RGB
    }

    public enum DuplexMode {
        DUPLEX_MODE_UNKNOWN,
        DUPLEX_MODE_SIMPLEX,
        DUPLEX_MODE_LONG_EDGE,
        DUPLEX_MODE_SHORT_EDGE
    }

    public abstract boolean isValid();

    public abstract boolean isReadOnly();

    public abstract CefPrintSettings copy();

    public abstract void setOrientation(boolean z);

    public abstract boolean isLandscape();

    public abstract void setPrinterPrintableArea(Dimension dimension, Rectangle rectangle, boolean z);

    public abstract void setDeviceName(String str);

    public abstract String getDeviceName();

    public abstract void setDPI(int i);

    public abstract int getDPI();

    public abstract void setPageRanges(Vector<CefPageRange> vector);

    public abstract int getPageRangesCount();

    public abstract void getPageRanges(Vector<CefPageRange> vector);

    public abstract void setSelectionOnly(boolean z);

    public abstract boolean isSelectionOnly();

    public abstract void setCollate(boolean z);

    public abstract boolean willCollate();

    public abstract void setColorModel(ColorModel colorModel);

    public abstract ColorModel getColorModel();

    public abstract void setCopies(int i);

    public abstract int getCopies();

    public abstract void setDuplexMode(DuplexMode duplexMode);

    public abstract DuplexMode getDuplexMode();

    public static final CefPrintSettings create() {
        return CefPrintSettings_N.createNative();
    }
}
