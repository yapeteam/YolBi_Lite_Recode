package org.cef.callback;

import org.cef.misc.BoolRef;
import org.cef.misc.IntRef;

public interface CefMenuModel {

    public static final class MenuId {
        public static final int MENU_ID_BACK = 100;
        public static final int MENU_ID_FORWARD = 101;
        public static final int MENU_ID_RELOAD = 102;
        public static final int MENU_ID_RELOAD_NOCACHE = 103;
        public static final int MENU_ID_STOPLOAD = 104;
        public static final int MENU_ID_UNDO = 110;
        public static final int MENU_ID_REDO = 111;
        public static final int MENU_ID_CUT = 112;
        public static final int MENU_ID_COPY = 113;
        public static final int MENU_ID_PASTE = 114;
        public static final int MENU_ID_DELETE = 115;
        public static final int MENU_ID_SELECT_ALL = 116;
        public static final int MENU_ID_FIND = 130;
        public static final int MENU_ID_PRINT = 131;
        public static final int MENU_ID_VIEW_SOURCE = 132;
        public static final int MENU_ID_SPELLCHECK_SUGGESTION_0 = 200;
        public static final int MENU_ID_SPELLCHECK_SUGGESTION_1 = 201;
        public static final int MENU_ID_SPELLCHECK_SUGGESTION_2 = 202;
        public static final int MENU_ID_SPELLCHECK_SUGGESTION_3 = 203;
        public static final int MENU_ID_SPELLCHECK_SUGGESTION_4 = 204;
        public static final int MENU_ID_SPELLCHECK_SUGGESTION_LAST = 204;
        public static final int MENU_ID_NO_SPELLING_SUGGESTIONS = 205;
        public static final int MENU_ID_USER_FIRST = 26500;
        public static final int MENU_ID_USER_LAST = 28500;
    }

    public enum MenuItemType {
        MENUITEMTYPE_NONE,
        MENUITEMTYPE_COMMAND,
        MENUITEMTYPE_CHECK,
        MENUITEMTYPE_RADIO,
        MENUITEMTYPE_SEPARATOR,
        MENUITEMTYPE_SUBMENU
    }

    boolean clear();

    int getCount();

    boolean addSeparator();

    boolean addItem(int i, String str);

    boolean addCheckItem(int i, String str);

    boolean addRadioItem(int i, String str, int i2);

    CefMenuModel addSubMenu(int i, String str);

    boolean insertSeparatorAt(int i);

    boolean insertItemAt(int i, int i2, String str);

    boolean insertCheckItemAt(int i, int i2, String str);

    boolean insertRadioItemAt(int i, int i2, String str, int i3);

    CefMenuModel insertSubMenuAt(int i, int i2, String str);

    boolean remove(int i);

    boolean removeAt(int i);

    int getIndexOf(int i);

    int getCommandIdAt(int i);

    boolean setCommandIdAt(int i, int i2);

    String getLabel(int i);

    String getLabelAt(int i);

    boolean setLabel(int i, String str);

    boolean setLabelAt(int i, String str);

    MenuItemType getType(int i);

    MenuItemType getTypeAt(int i);

    int getGroupId(int i);

    int getGroupIdAt(int i);

    boolean setGroupId(int i, int i2);

    boolean setGroupIdAt(int i, int i2);

    CefMenuModel getSubMenu(int i);

    CefMenuModel getSubMenuAt(int i);

    boolean isVisible(int i);

    boolean isVisibleAt(int i);

    boolean setVisible(int i, boolean z);

    boolean setVisibleAt(int i, boolean z);

    boolean isEnabled(int i);

    boolean isEnabledAt(int i);

    boolean setEnabled(int i, boolean z);

    boolean setEnabledAt(int i, boolean z);

    boolean isChecked(int i);

    boolean isCheckedAt(int i);

    boolean setChecked(int i, boolean z);

    boolean setCheckedAt(int i, boolean z);

    boolean hasAccelerator(int i);

    boolean hasAcceleratorAt(int i);

    boolean setAccelerator(int i, int i2, boolean z, boolean z2, boolean z3);

    boolean setAcceleratorAt(int i, int i2, boolean z, boolean z2, boolean z3);

    boolean removeAccelerator(int i);

    boolean removeAcceleratorAt(int i);

    boolean getAccelerator(int i, IntRef intRef, BoolRef boolRef, BoolRef boolRef2, BoolRef boolRef3);

    boolean getAcceleratorAt(int i, IntRef intRef, BoolRef boolRef, BoolRef boolRef2, BoolRef boolRef3);
}
