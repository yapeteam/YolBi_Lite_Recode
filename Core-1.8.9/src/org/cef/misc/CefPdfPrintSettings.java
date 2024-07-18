package org.cef.misc;

public class CefPdfPrintSettings {
    public boolean header_footer_enabled;
    public String header_footer_title;
    public String header_footer_url;
    public boolean landscape;
    public boolean backgrounds_enabled;
    public int page_width;
    public int page_height;
    public boolean selection_only;
    public int scale_factor;
    public double margin_top;
    public double margin_right;
    public double margin_bottom;
    public double margin_left;
    public MarginType margin_type;

    public enum MarginType {
        DEFAULT,
        NONE,
        MINIMUM,
        CUSTOM
    }

    public CefPdfPrintSettings m32clone() {
        CefPdfPrintSettings tmp = new CefPdfPrintSettings();
        tmp.header_footer_enabled = this.header_footer_enabled;
        tmp.header_footer_title = this.header_footer_title;
        tmp.header_footer_url = this.header_footer_url;
        tmp.landscape = this.landscape;
        tmp.backgrounds_enabled = this.backgrounds_enabled;
        tmp.page_width = this.page_width;
        tmp.page_height = this.page_height;
        tmp.selection_only = this.selection_only;
        tmp.scale_factor = this.scale_factor;
        tmp.margin_top = this.margin_top;
        tmp.margin_right = this.margin_right;
        tmp.margin_bottom = this.margin_bottom;
        tmp.margin_left = this.margin_left;
        tmp.margin_type = this.margin_type;
        return tmp;
    }
}
