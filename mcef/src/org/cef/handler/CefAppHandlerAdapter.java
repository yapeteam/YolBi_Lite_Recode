package org.cef.handler;

import org.cef.CefApp;
import org.cef.callback.CefCommandLine;
import org.cef.callback.CefSchemeRegistrar;

public abstract class CefAppHandlerAdapter implements CefAppHandler {
    private String[] args_;

    public CefAppHandlerAdapter(String[] args) {
        this.args_ = args;
    }

    @Override
    public void onBeforeCommandLineProcessing(String process_type, CefCommandLine command_line) {
        int i;
        if (process_type.isEmpty() && this.args_ != null) {
            boolean parseSwitchesDone = false;
            for (String arg : this.args_) {
                if (parseSwitchesDone || arg.length() < 2) {
                    command_line.appendArgument(arg);
                } else {
                    if (arg.startsWith("--")) {
                        i = 2;
                    } else {
                        i = arg.startsWith("/") ? 1 : arg.startsWith("-") ? 1 : 0;
                    }
                    int switchCnt = i;
                    switch (switchCnt) {
                        case 0:
                            command_line.appendArgument(arg);
                            continue;
                        case 2:
                            if (arg.length() == 2) {
                                parseSwitchesDone = true;
                                break;
                            }
                            break;
                    }
                    String[] switchVals = arg.substring(switchCnt).split("=");
                    if (switchVals.length == 2) {
                        command_line.appendSwitchWithValue(switchVals[0], switchVals[1]);
                    } else {
                        command_line.appendSwitch(switchVals[0]);
                    }
                }
            }
        }
    }

    @Override
    public boolean onBeforeTerminate() {
        return false;
    }

    @Override
    public void stateHasChanged(CefApp.CefAppState state) {
    }

    @Override
    public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
    }

    @Override
    public void onContextInitialized() {
    }

    @Override
    public CefPrintHandler getPrintHandler() {
        return null;
    }

    @Override
    public void onScheduleMessagePumpWork(long delay_ms) {
        CefApp.getInstance().doMessageLoopWork(delay_ms);
    }

    public void setArgs(String[] args) {
        this.args_ = args;
    }
}
