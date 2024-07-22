package org.cef;

import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.handler.CefAppHandler;
import org.cef.handler.CefAppHandlerAdapter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;

public class CefApp extends CefAppHandlerAdapter {
    private static CefApp self = null;
    private static CefAppHandler appHandler_ = null;
    private static CefAppState state_ = CefAppState.NONE;
    private Timer workTimer_;
    private HashSet<CefClient> clients_;
    private CefSettings settings_;

    public enum CefAppState {
        NONE,
        NEW,
        INITIALIZING,
        INITIALIZED,
        SHUTTING_DOWN,
        TERMINATED
    }

    private static native boolean N_Startup();

    public final native boolean N_PreInitialize();

    public final native boolean N_Initialize(String str, CefAppHandler cefAppHandler, CefSettings cefSettings);

    public final native void N_Shutdown();

    public final native void N_DoMessageLoopWork();

    private native CefVersion N_GetVersion();

    private native boolean N_RegisterSchemeHandlerFactory(String str, String str2, CefSchemeHandlerFactory cefSchemeHandlerFactory);

    private native boolean N_ClearSchemeHandlerFactories();

    public final class CefVersion {
        public final int JCEF_COMMIT_NUMBER;
        public final int CEF_VERSION_MAJOR;
        public final int CEF_VERSION_MINOR;
        public final int CEF_VERSION_PATCH;
        public final int CEF_COMMIT_NUMBER;
        public final int CHROME_VERSION_MAJOR;
        public final int CHROME_VERSION_MINOR;
        public final int CHROME_VERSION_BUILD;
        public final int CHROME_VERSION_PATCH;

        private CefVersion(int jcefCommitNo, int cefMajor, int cefMinor, int cefPatch, int cefCommitNo, int chrMajor, int chrMin, int chrBuild, int chrPatch) {
            this.JCEF_COMMIT_NUMBER = jcefCommitNo;
            this.CEF_VERSION_MAJOR = cefMajor;
            this.CEF_VERSION_MINOR = cefMinor;
            this.CEF_VERSION_PATCH = cefPatch;
            this.CEF_COMMIT_NUMBER = cefCommitNo;
            this.CHROME_VERSION_MAJOR = chrMajor;
            this.CHROME_VERSION_MINOR = chrMin;
            this.CHROME_VERSION_BUILD = chrBuild;
            this.CHROME_VERSION_PATCH = chrPatch;
        }

        public String getJcefVersion() {
            return this.CEF_VERSION_MAJOR + "." + this.CEF_VERSION_MINOR + "." + this.CEF_VERSION_PATCH + "." + this.JCEF_COMMIT_NUMBER;
        }

        public String getCefVersion() {
            return this.CEF_VERSION_MAJOR + "." + this.CEF_VERSION_MINOR + "." + this.CEF_VERSION_PATCH;
        }

        public String getChromeVersion() {
            return this.CHROME_VERSION_MAJOR + "." + this.CHROME_VERSION_MINOR + "." + this.CHROME_VERSION_BUILD + "." + this.CHROME_VERSION_PATCH;
        }

        public String toString() {
            return "JCEF Version = " + getJcefVersion() + "\nCEF Version = " + getCefVersion() + "\nChromium Version = " + getChromeVersion();
        }
    }

    private CefApp(String[] args, CefSettings settings) throws UnsatisfiedLinkError {
        super(args);
        this.workTimer_ = null;
        this.clients_ = new HashSet<>();
        this.settings_ = null;
        if (settings != null) {
            this.settings_ = settings.m14clone();
        }
        if (appHandler_ == null) {
            appHandler_ = this;
        }
        try {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (!CefApp.this.N_PreInitialize()) {
                        throw new IllegalStateException("Failed to pre-initialize native code");
                    }
                }
            };
            r.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addAppHandler(CefAppHandler appHandler) throws IllegalStateException {
        if (getState().compareTo(CefAppState.NEW) > 0) {
            throw new IllegalStateException("Must be called before CefApp is initialized");
        }
        appHandler_ = appHandler;
    }

    public static synchronized CefApp getInstance() throws UnsatisfiedLinkError {
        return getInstance(null, null);
    }

    public static synchronized CefApp getInstance(String[] args) throws UnsatisfiedLinkError {
        return getInstance(args, null);
    }

    public static synchronized CefApp getInstance(CefSettings settings) throws UnsatisfiedLinkError {
        return getInstance(null, settings);
    }

    public static synchronized CefApp getInstance(String[] args, CefSettings settings) throws UnsatisfiedLinkError {
        if (settings != null && getState() != CefAppState.NONE && getState() != CefAppState.NEW) {
            throw new IllegalStateException("Settings can only be passed to CEF before createClient is called the first time.");
        }
        if (self == null) {
            if (getState() == CefAppState.TERMINATED) {
                throw new IllegalStateException("CefApp was terminated");
            }
            self = new CefApp(args, settings);
            setState(CefAppState.NEW);
        }
        return self;
    }

    public final void setSettings(CefSettings settings) throws IllegalStateException {
        if (getState() != CefAppState.NONE && getState() != CefAppState.NEW) {
            throw new IllegalStateException("Settings can only be passed to CEF before createClient is called the first time.");
        }
        this.settings_ = settings.m14clone();
    }

    public final CefVersion getVersion() {
        try {
            return N_GetVersion();
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
            return null;
        }
    }

    public static CefAppState getState() {
        CefAppState cefAppState;
        synchronized (state_) {
            cefAppState = state_;
        }
        return cefAppState;
    }

    public static void setState(CefAppState state) {
        synchronized (state_) {
            state_ = state;
        }
        if (appHandler_ != null) {
            appHandler_.stateHasChanged(state);
        }
    }

    public static void forceShutdownState() {
        synchronized (state_) {
            state_ = CefAppState.SHUTTING_DOWN;
        }
    }

    public final synchronized void dispose() {
        switch (getState()) {
            case NEW:
                setState(CefAppState.TERMINATED);
                return;
            case INITIALIZING:
            case INITIALIZED:
                setState(CefAppState.SHUTTING_DOWN);
                if (this.clients_.isEmpty()) {
                    shutdown();
                    return;
                }
                HashSet<CefClient> clients = new HashSet<>(this.clients_);
                Iterator<CefClient> it = clients.iterator();
                while (it.hasNext()) {
                    CefClient c = it.next();
                    c.dispose();
                }
                return;
            case NONE:
            case SHUTTING_DOWN:
            case TERMINATED:
            default:
                return;
        }
    }

    public synchronized CefClient createClient() {
        switch (getState()) {
            case NEW:
                setState(CefAppState.INITIALIZING);
                initialize();
            case INITIALIZING:
            case INITIALIZED:
                CefClient client = new CefClient();
                this.clients_.add(client);
                return client;
            default:
                throw new IllegalStateException("Can't crate client in state " + state_);
        }
    }

    public boolean registerSchemeHandlerFactory(String schemeName, String domainName, CefSchemeHandlerFactory factory) {
        try {
            return N_RegisterSchemeHandlerFactory(schemeName, domainName, factory);
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean clearSchemeHandlerFactories() {
        try {
            return N_ClearSchemeHandlerFactories();
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public final synchronized void clientWasDisposed(CefClient client) {
        this.clients_.remove(client);
        if (this.clients_.isEmpty() && getState().compareTo(CefAppState.SHUTTING_DOWN) >= 0) {
            shutdown();
        }
    }

    private void initialize() {
        try {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    String library_path = CefApp.this.getJcefLibPath();
                    System.out.println("initialize on " + Thread.currentThread() + " with library path " + library_path);
                    CefSettings settings = CefApp.this.settings_ != null ? CefApp.this.settings_ : new CefSettings();
                    if (OS.isMacintosh()) {
                        if (settings.browser_subprocess_path == null) {
                            Path path = Paths.get(library_path, "../Frameworks/jcef Helper.app/Contents/MacOS/jcef Helper");
                            settings.browser_subprocess_path = path.normalize().toAbsolutePath().toString();
                        }
                    } else if (OS.isWindows()) {
                        if (settings.browser_subprocess_path == null) {
                            settings.browser_subprocess_path = library_path + "\\jcef_helper.exe";
                        }
                    } else if (OS.isLinux()) {
                        if (settings.browser_subprocess_path == null) {
                            settings.browser_subprocess_path = library_path + "/jcef_helper";
                        }
                        if (settings.resources_dir_path == null) {
                            settings.resources_dir_path = library_path;
                        }
                        if (settings.locales_dir_path == null) {
                            settings.locales_dir_path = library_path + "/locales";
                        }
                    }
                    if (CefApp.this.N_Initialize(library_path, CefApp.appHandler_, settings)) {
                        CefApp.setState(CefAppState.INITIALIZED);
                    }
                }
            };
            r.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final void handleBeforeTerminate() {
        System.out.println("Cmd+Q termination request.");
        CefAppHandler handler = appHandler_ == null ? this : appHandler_;
        if (!handler.onBeforeTerminate()) {
            dispose();
        }
    }

    private void shutdown() {
        System.out.println("shutdown on " + Thread.currentThread());
        setState(CefAppState.TERMINATED);
        self = null;
    }

    public final void doMessageLoopWork(final long delay_ms) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (CefApp.getState() == CefAppState.TERMINATED) {
                    return;
                }
                if (CefApp.this.workTimer_ != null) {
                    CefApp.this.workTimer_.stop();
                    CefApp.this.workTimer_ = null;
                }
                if (delay_ms <= 0) {
                    CefApp.this.N_DoMessageLoopWork();
                    CefApp.this.doMessageLoopWork(33L);
                    return;
                }
                long timer_delay_ms = delay_ms;
                if (timer_delay_ms > 33) {
                    timer_delay_ms = 33;
                }
                CefApp.this.workTimer_ = new Timer((int) timer_delay_ms, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        CefApp.this.workTimer_.stop();
                        CefApp.this.workTimer_ = null;
                        CefApp.this.N_DoMessageLoopWork();
                        CefApp.this.doMessageLoopWork(33L);
                    }
                });
                CefApp.this.workTimer_.start();
            }
        });
    }

    public static boolean startup() {
        if (OS.isLinux() || OS.isMacintosh()) {
            return N_Startup();
        }
        return true;
    }

    public final String getJcefLibPath() {
        String library_path = System.getProperty("java.library.path");
        String[] paths = library_path.split(System.getProperty("path.separator"));
        for (String path : paths) {
            File dir = new File(path);
            String[] found = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir2, String name) {
                    return name.equalsIgnoreCase("libjcef.dylib") || name.equalsIgnoreCase("libjcef.so") || name.equalsIgnoreCase("jcef.dll");
                }
            });
            if (found != null && found.length != 0) {
                return path;
            }
        }
        return library_path;
    }
}
