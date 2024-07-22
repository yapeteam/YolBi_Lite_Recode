package org.cef;

public class CefSettings {
    public String browser_subprocess_path = null;
    public boolean windowless_rendering_enabled = true;
    public boolean command_line_args_disabled = false;
    public String cache_path = null;
    public boolean persist_session_cookies = false;
    public String user_agent = null;
    public String product_version = null;
    public String locale = null;
    public String log_file = null;
    public LogSeverity log_severity = LogSeverity.LOGSEVERITY_DEFAULT;
    public String javascript_flags = null;
    public String resources_dir_path = null;
    public String locales_dir_path = null;
    public boolean pack_loading_disabled = false;
    public int remote_debugging_port = 0;
    public int uncaught_exception_stack_size = 0;
    public boolean ignore_certificate_errors = false;
    public ColorType background_color = null;

    public enum LogSeverity {
        LOGSEVERITY_DEFAULT,
        LOGSEVERITY_VERBOSE,
        LOGSEVERITY_INFO,
        LOGSEVERITY_WARNING,
        LOGSEVERITY_ERROR,
        LOGSEVERITY_FATAL,
        LOGSEVERITY_DISABLE
    }

    public static class ColorType {
        private long color_value;

        private ColorType() {
            this.color_value = 0L;
        }

        public ColorType(int alpha, int red, int green, int blue) {
            this.color_value = ((long) alpha << 24) | ((long) red << 16) | ((long) green << 8) | (blue);
        }

        public long getColor() {
            return this.color_value;
        }

        public ColorType m15clone() {
            ColorType res = new ColorType();
            res.color_value = this.color_value;
            return res;
        }
    }

    public CefSettings m14clone() {
        CefSettings tmp = new CefSettings();
        tmp.browser_subprocess_path = this.browser_subprocess_path;
        tmp.windowless_rendering_enabled = this.windowless_rendering_enabled;
        tmp.command_line_args_disabled = this.command_line_args_disabled;
        tmp.cache_path = this.cache_path;
        tmp.persist_session_cookies = this.persist_session_cookies;
        tmp.user_agent = this.user_agent;
        tmp.product_version = this.product_version;
        tmp.locale = this.locale;
        tmp.log_file = this.log_file;
        tmp.log_severity = this.log_severity;
        tmp.javascript_flags = this.javascript_flags;
        tmp.resources_dir_path = this.resources_dir_path;
        tmp.locales_dir_path = this.locales_dir_path;
        tmp.pack_loading_disabled = this.pack_loading_disabled;
        tmp.remote_debugging_port = this.remote_debugging_port;
        tmp.uncaught_exception_stack_size = this.uncaught_exception_stack_size;
        tmp.ignore_certificate_errors = this.ignore_certificate_errors;
        if (this.background_color != null) {
            tmp.background_color = this.background_color.m15clone();
        }
        return tmp;
    }
}
