package org.cef.handler;

import org.cef.browser.CefBrowser;
import org.cef.misc.BoolRef;

public interface CefKeyboardHandler {
    boolean onPreKeyEvent(CefBrowser cefBrowser, CefKeyEvent cefKeyEvent, BoolRef boolRef);

    boolean onKeyEvent(CefBrowser cefBrowser, CefKeyEvent cefKeyEvent);

    public static final class CefKeyEvent {
        public final EventType type;
        public final int modifiers;
        public final int windows_key_code;
        public final int native_key_code;
        public final boolean is_system_key;
        public final char character;
        public final char unmodified_character;
        public final boolean focus_on_editable_field;

        public enum EventType {
            KEYEVENT_RAWKEYDOWN,
            KEYEVENT_KEYDOWN,
            KEYEVENT_KEYUP,
            KEYEVENT_CHAR
        }

        CefKeyEvent(EventType typeAttr, int modifiersAttr, int windows_key_codeAttr, int native_key_codeAttr, boolean is_system_keyAttr, char characterAttr, char unmodified_characterAttr, boolean focus_on_editable_fieldAttr) {
            this.type = typeAttr;
            this.modifiers = modifiersAttr;
            this.windows_key_code = windows_key_codeAttr;
            this.native_key_code = native_key_codeAttr;
            this.is_system_key = is_system_keyAttr;
            this.character = characterAttr;
            this.unmodified_character = unmodified_characterAttr;
            this.focus_on_editable_field = focus_on_editable_fieldAttr;
        }

        public String toString() {
            return "CefKeyEvent [type=" + this.type + ", modifiers=" + this.modifiers + ", windows_key_code=" + this.windows_key_code + ", native_key_code=" + this.native_key_code + ", is_system_key=" + this.is_system_key + ", character=" + this.character + ", unmodified_character=" + this.unmodified_character + ", focus_on_editable_field=" + this.focus_on_editable_field + "]";
        }
    }
}
