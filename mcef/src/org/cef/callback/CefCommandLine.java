package org.cef.callback;

import java.util.Map;
import java.util.Vector;

public interface CefCommandLine {
    void reset();

    String getProgram();

    void setProgram(String str);

    boolean hasSwitches();

    boolean hasSwitch(String str);

    String getSwitchValue(String str);

    Map<String, String> getSwitches();

    void appendSwitch(String str);

    void appendSwitchWithValue(String str, String str2);

    boolean hasArguments();

    Vector<String> getArguments();

    void appendArgument(String str);
}
