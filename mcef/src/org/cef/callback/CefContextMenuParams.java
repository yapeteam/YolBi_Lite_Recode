package org.cef.callback;

import java.util.Vector;

public interface CefContextMenuParams {

    public static final class EditStateFlags {
        public static final int CM_EDITFLAG_NONE = 0;
        public static final int CM_EDITFLAG_CAN_UNDO = 1;
        public static final int CM_EDITFLAG_CAN_REDO = 2;
        public static final int CM_EDITFLAG_CAN_CUT = 4;
        public static final int CM_EDITFLAG_CAN_COPY = 8;
        public static final int CM_EDITFLAG_CAN_PASTE = 16;
        public static final int CM_EDITFLAG_CAN_DELETE = 32;
        public static final int CM_EDITFLAG_CAN_SELECT_ALL = 64;
        public static final int CM_EDITFLAG_CAN_TRANSLATE = 128;
    }

    public static final class MediaStateFlags {
        public static final int CM_MEDIAFLAG_NONE = 0;
        public static final int CM_MEDIAFLAG_ERROR = 1;
        public static final int CM_MEDIAFLAG_PAUSED = 2;
        public static final int CM_MEDIAFLAG_MUTED = 4;
        public static final int CM_MEDIAFLAG_LOOP = 8;
        public static final int CM_MEDIAFLAG_CAN_SAVE = 16;
        public static final int CM_MEDIAFLAG_HAS_AUDIO = 32;
        public static final int CM_MEDIAFLAG_HAS_VIDEO = 64;
        public static final int CM_MEDIAFLAG_CONTROL_ROOT_ELEMENT = 128;
        public static final int CM_MEDIAFLAG_CAN_PRINT = 256;
        public static final int CM_MEDIAFLAG_CAN_ROTATE = 512;
    }

    public enum MediaType {
        CM_MEDIATYPE_NONE,
        CM_MEDIATYPE_IMAGE,
        CM_MEDIATYPE_VIDEO,
        CM_MEDIATYPE_AUDIO,
        CM_MEDIATYPE_FILE,
        CM_MEDIATYPE_PLUGIN
    }

    public static final class TypeFlags {
        public static final int CM_TYPEFLAG_NONE = 0;
        public static final int CM_TYPEFLAG_PAGE = 1;
        public static final int CM_TYPEFLAG_FRAME = 2;
        public static final int CM_TYPEFLAG_LINK = 4;
        public static final int CM_TYPEFLAG_MEDIA = 8;
        public static final int CM_TYPEFLAG_SELECTION = 16;
        public static final int CM_TYPEFLAG_EDITABLE = 32;
    }

    int getXCoord();

    int getYCoord();

    int getTypeFlags();

    String getLinkUrl();

    String getUnfilteredLinkUrl();

    String getSourceUrl();

    boolean hasImageContents();

    String getPageUrl();

    String getFrameUrl();

    String getFrameCharset();

    MediaType getMediaType();

    int getMediaStateFlags();

    String getSelectionText();

    String getMisspelledWord();

    boolean getDictionarySuggestions(Vector<String> vector);

    boolean isEditable();

    boolean isSpellCheckEnabled();

    int getEditStateFlags();
}
